package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.domain.{Action, Activity, Status}
import com.itiancai.galaxy.dts.store.{ActionDao, ActivityDao}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._


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
    val count = activityDao.updateStatusByTxIdStatus(txId, status.getStatus, Status.Activity.UNKNOWN.getStatus)
    count == 1
  }

  /**
    * update activity finish = 1
    *
    * @param txId
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def finishActivity(txId: String):Unit = {
    activityDao.finishActivity(txId)
  }

  /**
    * 开始主事务
    *
    * @param activity
    */
  @Transactional(value = "dtsTransactionManager")
  def saveActivity(activity:Activity): Unit = {
    activityDao.save(activity)
  }

  /**
    * handle tx finish = 2(处理中)
    *
    * @param txId
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def lockTX(txId: String, status: Status.Activity): Boolean = {
    logger.info(s"lockTXByTxIdAndStatus:${txId}")
    activityDao.lockTXByTxIdAndStatus(txId, status, 10) == 1
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

  /**
    * 完成子事务(提交或回滚)
    *
    * @param actionId
    * @param status
    * @return
    */
  @Transactional(value = "dtsTransactionManager")
  def finishAction(actionId: String, status: Status.Action): Boolean = {
    val count = actionDao.updateStatusByIdStatus(actionId, status.getStatus, Status.Action.PREPARE.getStatus)
    count == 1
  }


  /**
    * 开启子事务
    *
    * @param action
    */
  @Transactional(value = "dtsTransactionManager")
  def saveAction(action:Action): Unit = {
    actionDao.save(action)
  }

  /**
    * 子事务prepare
    * @param actionId
    */
  @Transactional(value = "dtsTransactionManager")
  def prepareAction(actionId: String) {
    logger.info(s"prepare action start actionId:${actionId}")
    actionDao.updateStatusByIdStatus(actionId, Status.Action.PREPARE.getStatus, Status.Action.UNKNOWN.getStatus)
    logger.info(s"prepare action end, actionId:${actionId}")
  }


  /**
    * 查询所有未完成的事务
    *
    * @return
    */
  def listUnfinished(index: Int, total:Int): Seq[String] = {
    try {
      val list1 = activityDao.listSuccessOrFail(index, total, 10)
      val list2 = activityDao.listUnknownAndTimeout(index, total, 10)
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
  def reclaimHandleTimeoutTX(timeout:Int): Unit = {
    val count = activityDao.reclaimHandleTimeout(timeout)
    logger.info(s"reclaimHandleTimeoutTX count:${count}")
  }



  def listActionByTxId(txId:String):List[Action] = {
    actionDao.listByTxId(txId).toList
  }
}


