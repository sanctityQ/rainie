package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.store.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain.Status
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DTSRepository {

  @Autowired
  val activityDao: ActivityDao = null
  @Autowired
  val actionDao: ActionDao = null

  val logger = LoggerFactory.getLogger(getClass)

  /**
    * 同步主事务状态
    *
    * @param txId
    * @param status
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def updateActivityStatus(txId: String, status: Status.Activity): Boolean = {
    val count = activityDao.updateStatus(txId, status.getStatus, Status.Activity.UNKNOWN.getStatus)
    count == 1
  }

  /**
    * 完成子事务(提交或回滚)
    *
    * @param actionId
    * @param status
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def finishAction(actionId: String, status: Status.Action): Boolean = {
    val count = actionDao.updateStatus(actionId, status.getStatus, Status.Action.PREPARE.getStatus)
    count == 1
  }

  /**
    * update activity finish = 1
    *
    * @param txId
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def finishActivity(txId: String):Boolean = {
    val count = activityDao.finishActivity(txId)
    count == 1
  }

  /**
    * handle tx finish = 2(处理中)
    *
    * @param txId
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def handleTX(txId: String): Boolean = {
    logger.info(s"handleTX:${txId}")
    activityDao.handle(txId, 10) == 1
  }

  /**
    * reclaim tx finish = 0(未处理)
    *
    * @param txId
    */
  @Transactional(value = "dtsTransactionManager")
  def reclaimTX(txId: String): Unit = {
    activityDao.reclaim(txId)
  }

}


