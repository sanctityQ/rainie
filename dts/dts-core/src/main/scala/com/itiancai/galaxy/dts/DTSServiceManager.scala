package com.itiancai.galaxy.dts

import java.util.Date

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain._
import com.twitter.util.Future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConversions._

@Service
class DTSServiceManager {
  @Autowired
  private val actionDao: ActionDao = null
  @Autowired
  private val activityDao: ActivityDao = null
  @Autowired
  private val idGenerator: IdGenerator = null

  /**
    * 开启主事务,流程如下
    * 1.判断name对应的bean是否存在,且缓存
    * 2.生成tx_id,tx_id并存放在域中
    * 3.组装数据并保存
    *
    * @param bizId        业务id
    * @param businessType 服务名称
    * @param timeOut      超时时间
    * @throws Exception
    */
  @Transactional
  def startActivity(bizId: String, businessType: String, timeOut: Int):String ={
    val txId: String = idGenerator.getTxId(businessType)
    val activity: Activity = new Activity(txId, Status.Activity.UNKNOWN, businessType, new Date, timeOut, new Date, 0, bizId)
    activityDao.save(activity)
    txId
  }

  /**
    * 主事务处理成功,子事务做commit 流程:
    * 1.tx_id获取当前主事务数据,修改数据状态
    * 2.变更子事务状态
    * 3.isImmediately true action回调对应得commit/rollback
    *
    * @param status        方法名称 SUCCESS FAIL
    * @param isImmediately 是否实时处理
    * @throws Exception
    */
  def finishActivity(status: Status.Activity, isImmediately: Boolean) {
    val txId: String = TXIdLocal.current_txId
    updateActivityAction(status, txId)
    if (isImmediately) {
      val actionList = actionDao.findByTxId(txId)
      executeAction(actionList)
    }
  }

  /**
    * 远程调用子事务commit rollback方法
    *
    * @param list
    */
  @Transactional
  def executeAction(list: Seq[Action]) {
    if (Option(list).isDefined && list.length != 0) {
      Future.collect(list.map(action =>{
        //TODO HTTPClient调用 与小宝公用一个方法
        Future(false)
      })).map(resultList =>{
        if(!resultList.contains(false)) {
          val activity: Activity = activityDao.findByTxId(list.get(0).getTxId)
          activityDao.updateActivityFinish(activity.getTxId ,activity.getStatus.getStatus)
        }
      })
    }
  }

  /**90
    * 修改activity action数据
    *
    * @param status
    * @param txId
    */
  @Transactional
  private def updateActivityAction(status: Status.Activity, txId: String) {
    val activity: Activity = activityDao.findByTxId(txId)
    if (activity != null) {
      activityDao.updateAcvityStatus(activity.getTxId,status.getStatus,activity.getStatus.getStatus)
    }
  }

  /**
    * 开启子事务,流程如下:
    * 1.判断name对应的bean及tx_id是否存在,且缓存
    * 2.组装Action 保存数据
    *
    * @param instructionId 幂等id
    * @param serviceName   服务名称
    * @param context       请求参数json
    */
  @Transactional
  def startAction(instructionId: String, serviceName: String, context: String): String = {
    val actionId: String = idGenerator.getActionId(serviceName)
    val action: Action = new Action(TXIdLocal.current_txId, actionId, Status.Action.UNKNOWN, serviceName,
      new Date(), new Date(), context, instructionId)
    actionDao.save(action)
    action.getActionId
  }

  /**
    * 子事务处理状态变更处理
    *
    * @param status 事务变更状态 PREPARE FAIL
    * @throws Exception
    */
  @Transactional
  def finishAction(status: Status.Action, actionId: String) {
    val action: Action = actionDao.findByActionId(actionId)
    actionDao.updateActionStatus(action.getActionId,status.getStatus,action.getStatus.getStatus)
  }
}
