package com.itiancai.galaxy.dts.recovery

import java.util.concurrent.{Executors, TimeUnit}
import javax.inject.Inject

import com.itiancai.galaxy.dts.TransactionManager
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.http.ActivityStatusRequest
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.support.{DefaultTransactionStatus, DtsXid, ServiceName, XidFactory}
import com.twitter.util.{Await, Future}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

trait Task {
  def execute()
}

/**
  * 补偿事务任务
  */
@Component("recoveryTask")
class RecoveryTask @Inject()
(
  txManager: TransactionManager,
  dtsRepository: DTSRepository,
  clientSource: HttpClientSource
) extends Task {

  val logger = LoggerFactory.getLogger(getClass)

  /* 补偿queue key*/
  @Value("${tx.recovery.poolSize}")
  val recoveryPoolSize: Int = 0

  @Value("${tx.recovery.interval}")
  val recoveryInterval: Int = 0

  override def execute(): Unit = {
    val recoveryScheduled = Executors.newScheduledThreadPool(recoveryPoolSize)
    for (index <- 0 until recoveryPoolSize) {
      recoveryScheduled.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = {
          val list = recovery(index, recoveryPoolSize)
          if (list.isEmpty) {
            Thread.sleep(1000)
          }
        }
      }, 1000, 100, TimeUnit.MILLISECONDS)
    }
  }

  /**
    * 补偿事务
    *
    * @param index
    * @param total
    */
  def recovery(index: Int, total: Int): Seq[String] = {
    /**
      * 同步主事务状态
      *
      * @return
      */
    def synchroActivityStatus(txId: String): Future[Status.Activity] = {
      val activity = dtsRepository.findActivity(txId)
      if (activity.getStatus != Status.Activity.UNKNOWN.getStatus) {
        Future(Status.Activity.getStatus(activity.getStatus))
      } else {
        val recoveryService = ServiceName(activity.getBusinessType)
        val activityRequest = ActivityStatusRequest(recoveryService.serviceName, activity.getBusinessId)
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
    //查询未完成的事务
    val txList = dtsRepository.listUnfinished(index, total, recoveryInterval)
    if (!txList.isEmpty) {
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
          case t: Throwable => {
            logger.error(s"Consumer ${index} handle tx[${txId_}]  error", t)
          }
        }
        logger.info(s"Consumer ${index} handle tx[${txId_}] end")
      })
    }
    txList
  }
}

/**
  * 回收超时处理任务
  */
@Component("reclaimTask")
class ReclaimTask @Inject()
(
  dtsRepository: DTSRepository
) extends Task {

  val logger = LoggerFactory.getLogger(getClass)

  override def execute(): Unit = {
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

}

/**
  * 报警任务
  */
@Component("alarmTask")
class AlarmTask extends Task {

  override def execute(): Unit = {

  }

}
