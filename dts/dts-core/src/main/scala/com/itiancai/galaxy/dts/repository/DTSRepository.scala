package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain.Status
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
  * Created by bao on 16/8/3.
  */
@Component
class DTSRepository {

  val logger = LoggerFactory.getLogger(getClass)

  @Autowired
  val activityDao: ActivityDao = null

  @Autowired
  val actionDao: ActionDao = null

  /**
    * 同步主事务状态
    * @param txId
    * @param status
    * @return
    */
  @Transactional
  def updateActivityStatus(txId: String, status: Status.Activity): Boolean = {
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


