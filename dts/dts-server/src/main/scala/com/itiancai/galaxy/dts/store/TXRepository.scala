package com.itiancai.galaxy.dts.store

import com.itiancai.galaxy.dts.store.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.utils.{CollectException, RedisService}
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

/**
  * Created by bao on 16/8/11.
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
      case t: Throwable => {
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
        if (!flag) {
          throw new CollectException("redisService push error")
        }
        true
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
    * 消费一个TX
 *
    * @return txId
    */
  @Transactional
  def consumerTX(): String = {
    try {
      val txId = redisService.rpop(compensateQueue)
      if(StringUtils.isNotBlank(txId)) {
        activityDao.handle(txId)
      }
      txId
    } catch {
      case t: Throwable => {
        logger.error(s"redisService rpop ${compensateQueue} error", t)
        null
      }
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
    *
    * @param txId
    */
  @Transactional
  def reclaimTX(txId: String): Unit = {
    activityDao.reclaim(txId)
  }
}
