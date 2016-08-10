package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.utils._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

/**
  * Created by bao on 16/8/3.
  */
@Component
class TXRepository {

  val logger = LoggerFactory.getLogger(getClass)

  @Autowired
  val activityDao: ActivityDao = null

  @Autowired
  val actionDao: ActionDao = null

  @Autowired
  val redisService: RedisService = null

  /* 补偿queue key*/
  @Value("${tx.compensate.queue}")
  val compensateQueue: String = null

  /* 补偿queue key*/
  @Value("${tx.compensate.queue.maxSize}")
  val compensateQueueMax: Int = 0

  //TODO 配置
  /* 最大重试次数*/
  val maxRetryCount: Int = 10

  //TODO 配置
  /* 处理超时时间*/
  val handleTimeout: Int = 60

  /**
    * 查询所有未完成的事务
    *
    * @return
    */
  def listUnfinished(): Seq[String] = {
    try {
      val list1 = activityDao.listSuccessOrFail(maxRetryCount)
      val list2 = activityDao.listUnknownAndTimeout(maxRetryCount)
      (list1 ++ list2).toList
    } catch {
      case t:Throwable => {
        logger.error("listUnfinished fail", t)
        List()
      }
    }
  }

  /**
    * collect tx to queue
    *
    * @param txId
    * @return
    */
  @Transactional
  def collectTX(txId: String): Boolean = {
    //TODO queue maxSize control
    logger.info(s"collectTX:${txId}")
    //update collect = 1
    if (activityDao.collect(txId, maxRetryCount) == 1) {
      try {
        //push txId to queue
        val flag = redisService.lpush(compensateQueue, txId) > 0
        if(!flag) {
          throw new CollectException("redisService push error")
        }
        flag
      } catch {
        case t: Throwable => {
          //collect fail, rollback collect flag
          logger.error(s"collectTX:${txId} push error", t)
          throw new CollectException("collect error", t)
        }
      }
    } else {
      //tx status had changed
      logger.warn(s"activity:${txId} status had changed")
      false
    }
  }

  /**
    * reclaim tx collect = 1 and handle timeout
    */
  @Transactional
  def reclaimHandleTimeoutTX(): Unit = {
    val count = activityDao.reclaimHandleTimeout(handleTimeout)
    logger.info(s"reclaimHandleTimeoutTX count:${count}")
  }

  /**
    * reclaim tx collect = 0
    * @param txId
    */
  @Transactional
  def reclaimTX(txId: String): Unit = {
    activityDao.reclaim(txId)
  }

  /**
    * 同步主事务状态
    * @param txId
    * @param status
    * @return
    */
  @Transactional
  def synchroActivityStatus(txId: String, status: Status.Activity): Boolean = {
    val count = activityDao.updateStatus(txId, status.getStatus, Status.Activity.UNKNOWN.getStatus)
    val flag = (count == 1)
    if(flag) {
      logger.info(s"tx:[${txId}] updateStatus success")
    } else {
      logger.info(s"tx:[${txId}] status had changed")
    }
    flag
  }

  /**
    * 完成子事务(提交或回滚)
    *
    * @param actionId
    * @param status
    * @return
    */
  @Transactional
  def finishAction(actionId: String, status: Status.Action): Boolean = {
    val count = actionDao.updateStatus(actionId, status.getStatus, Status.Action.PREPARE.getStatus)
    if(count == 1) {
      true
    } else {
      val entity = actionDao.findByActionId(actionId)
      entity.getStatus == status
    }
  }

  /**
    * update activity finish = 1
    * @param txId
    * @return
    */
  @Transactional
  def finishActivity(txId: String) = {
    val count = activityDao.finishActivity(txId)
    if(count == 1) {
      true
    } else {
      val activity = activityDao.findByTxId(txId)
      activity.getFinish == 1
    }
  }

}


