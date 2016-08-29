package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.store.{ActionDao, ActivityDao}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

@Component
class TXRepository {

  val logger = LoggerFactory.getLogger(getClass)

  @Autowired
  val activityDao: ActivityDao = null
  @Autowired
  val actionDao: ActionDao = null

  //TODO 配置
  /* 最大重试次数*/
  val maxRetryCount: Int = 10

  //TODO 配置
  /* 处理超时时间*/
  val handleTimeout: Int = 10

  /* 补偿queue key*/
  @Value("${tx.compensate.consumer.poolSize}")
  val consumerPoolSize :Int = 0

  /**
    * 查询所有未完成的事务
    *
    * @return
    */
  def listUnfinished(index: Int): Seq[String] = {
    try {
      val list1 = activityDao.listSuccessOrFail(index, consumerPoolSize, maxRetryCount)
      val list2 = activityDao.listUnknownAndTimeout(index, consumerPoolSize, maxRetryCount)
      (list1 ++ list2).toList
    } catch {
      case t: Throwable => {
        logger.error("listUnfinished fail", t)
        List()
      }
    }
  }

  /**
    * reclaim tx collect = 1 and handle timeout
    */
  @Transactional(value = "dtsTransactionManager")
  def reclaimHandleTimeoutTX(): Unit = {
    val count = activityDao.reclaimHandleTimeout(handleTimeout)
    logger.info(s"reclaimHandleTimeoutTX count:${count}")
  }
}
