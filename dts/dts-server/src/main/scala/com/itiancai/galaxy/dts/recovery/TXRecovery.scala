package com.itiancai.galaxy.dts.recovery

import java.util.concurrent.{Executors, TimeUnit}
import javax.annotation.PostConstruct
import javax.inject.Inject

import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.support.{DefaultTransactionStatus, DtsXid, XidFactory}
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
          if(txList.isEmpty) {
            logger.debug(s"Consumer ${index} handle sleep")
            Thread.sleep(1000)
          } else {
            logger.info(s"Consumer ${index} handle txList:[${txList}]")
            txList.foreach(txId_ => {
              //处理tx
              logger.info(s"Consumer ${index} handle tx[${txId_}] begin")
              try {
                val result_f = synchroActivityStatus(txId_).map(status => {
                  val xid = new DtsXid(txId_, XidFactory.noBranchQualifier)
                  val txStatus = new DefaultTransactionStatus(xid)
                  dtsRepository.listActionByTxId(txId_)
                    .foreach({ action =>
                      val branchId = new DtsXid(txId_, action.getActionId)
                      txStatus.addResourceXid(branchId)
                    })
                  status match {
                    case Status.Activity.SUCCESS => {
                      //提交事务
                      txManager.commit(txStatus)
                    }
                    case Status.Activity.FAIL => {
                      //回归事务
                      txManager.rollback(txStatus)
                    }
                    case _ => {
                      //同步状态失败,增加处理次数
                      dtsRepository.incrementRetryCountByTxId(txId_)
                      logger.warn(s"Consumer ${index} synchroActivityStatus tx[${txId_}] status${status}")
                    }
                  }
                })
                Await.result(result_f)
              } catch {
                case t:Throwable => {
                  logger.error(s"Consumer ${index} handle tx[${txId_}]  error", t)
                }
              }
              logger.info(s"Consumer ${index} handle tx[${txId_}] end")
            })
          }
        }
      }, 1000, 100, TimeUnit.MILLISECONDS)
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
    }, 1000, 10000, TimeUnit.MILLISECONDS)
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
      val activityRequest = ActivityStatusRequest(activity.getBusinessType, activity.getBusinessId)
      clientSource.getTransactionClient(recoveryService).request(activityRequest)
        .map(status => {
          status match {
            //成功
            case 0 => Status.Activity.SUCCESS
            //1失败
            case 1 => Status.Activity.FAIL
            //TODO 其他?
            case _ => Status.Activity.UNKNOWN
          }
        })
        .handle({
          case t: Throwable => Status.Activity.UNKNOWN
        })
    }
  }
}

