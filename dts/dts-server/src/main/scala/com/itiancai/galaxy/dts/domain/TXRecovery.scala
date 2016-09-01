package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, TimeUnit}
import javax.annotation.PostConstruct
import javax.inject.Inject

import com.itiancai.galaxy.dts.recovery.{ActivityStatusRequest, RecoverServiceName, RecoveryClientSource}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.{TransactionManager, TransactionStatus}
import com.twitter.util.{Await, Future}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TXRecovery @Inject()
(
  txManager: TransactionManager,
  dtsRepository: DTSRepository,
  clientSource: RecoveryClientSource
){

  val logger = LoggerFactory.getLogger(getClass)

  /* 补偿queue key*/
  @Value("${tx.compensate.consumer.poolSize}")
  val consumerPoolSize :Int = 0

  @PostConstruct
  def init(): Unit = {
    //补偿事务
    val recoveryScheduled = Executors.newScheduledThreadPool(consumerPoolSize)
    for(index <- 0 until consumerPoolSize) {
      recoveryScheduled.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = {
          val txList = dtsRepository.listUnfinished(index, consumerPoolSize)
          txList.foreach(txId => {
            //处理tx
            logger.info(s"Consumer ${index} handle tx[${txId}] begin")
            try {
              val result_f = synchroActivityStatus(txId).map(status => {
                status match {
                  case Status.Activity.SUCCESS => {
                    txManager.commit(new TransactionStatus{
                      override def txId(): String = txId
                    })
                  }
                  case Status.Activity.FAIL => {
                    txManager.rollback(new TransactionStatus{
                      override def txId(): String = txId
                    })
                  }
                  case _ => {
                    logger.warn(s"Consumer ${index} synchroActivityStatus tx[${txId}] status${status}")
                  }
                }
              })
              Await.result(result_f)
            } catch {
              case t:Throwable => {
                logger.error(s"Consumer ${index} handle tx[${txId}]  error", t)
              }
            }
            logger.info(s"Consumer ${index} handle tx[${txId}] end")
          })
        }
      }, 1000, 500, TimeUnit.MILLISECONDS)
    }

    //回收处理超时任务
    val reclaimScheduled = Executors.newSingleThreadScheduledExecutor
    reclaimScheduled.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        logger.info("TXProducer execute start ...")
        //回收处理超时任务
        dtsRepository.reclaimHandleTimeoutTX(10)
        logger.info("TXProducer execute end ...")
      }
    }, 1, 10, TimeUnit.SECONDS)
  }


  /**
    * 同步主事务状态
    * @return
    */
  def synchroActivityStatus(txId: String): Future[Status.Activity] = {
    //获取txId
    val activity = dtsRepository.findActivity(txId)
    if (activity.getStatus != Status.Activity.UNKNOWN.getStatus) {
      Future(Status.Activity.getStatus(activity.getStatus))
    } else {
      val recoveryService = RecoverServiceName.parse(activity.getBusinessType)
      val activityRequest = ActivityStatusRequest(recoveryService.serviceName, activity.getBusinessId)
      clientSource.getTransactionClient(recoveryService).request(activityRequest)
        .map(status => {
          status match {
            //成功
            case 0 => Status.Activity.SUCCESS
            //1失败
            case 1 => Status.Activity.FAIL
            //TODO 其他?
            case _ => throw new RuntimeException("SynchroException")
          }
        })
    }
  }
}