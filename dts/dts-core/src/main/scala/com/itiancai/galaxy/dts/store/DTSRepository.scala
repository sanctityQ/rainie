package com.itiancai.galaxy.dts.store

import javax.inject.Inject

import com.itiancai.galaxy.dts.store.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.domain.Status.ActionStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DTSRepository {

  val logger = LoggerFactory.getLogger(getClass)

  @Autowired
  val activityDao: ActivityDao = null;

  @Autowired
  val actionDao: ActionDao = null;

  /**
    * 同步主事务状态
    *
    * @param txId
    * @param status
    * @return
    */
  def updateActivityStatus(txId: String)(f:Unit => n): Boolean = {
    val count = activityDao.updateStatus(txId, status.value(), Status.Activity.INIT.value())
    val flag = (count == 1)
    if (flag) {
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
  def finishAction(actionId: String, status: ActionStatus): Boolean = {
    val count = actionDao.updateStatus(actionId, status.value(), Status.ActionStatus.PREPARE.value())
    if (count == 1) {
      true
    } else {
      val entity = actionDao.findByActionId(actionId)
      entity.getStatus == status
    }
  }

  /**
    * update activity finish = 1
    *
    * @param txId
    * @return
    */
  @Transactional
  def finishActivity(txId: String) = {
    val count = activityDao.finishActivity(txId)
    if (count == 1) {
      true
    } else {
      val activity = activityDao.findByTxId(txId)
      activity.getFinish == 1
    }
  }

}


