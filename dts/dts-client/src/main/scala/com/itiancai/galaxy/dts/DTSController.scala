package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.domain.Status.Action
import com.itiancai.galaxy.dts.utils.NameChecker
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DTSController {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  @Autowired
  private val manager: DTSServiceManager = null
  @Autowired
  private val nameChecker: NameChecker = null


  /**
    * 开启主事务,流程:
    * 1.生成id并存放在TreadLocal
    * 2.开启主事务(主事务状态为UNKNOWN)
    *
    * @param businessId   业务id
    * @param businessType    服务名称
    * @param timeOut 超时时间
    */
  def startActivity(businessId: String, businessType: String, timeOut: Int): String = {
    logger.info("DTSController.startActivity Paramter{bizId=" + businessId + ",typeName=" + businessType + ",timeout=" + timeOut)
    manager.startActivity(businessId, businessType, timeOut)
  }

  /**
    * 主事务处理成功,子事务做SUCCESS或FIAL处理,流程如下:
    * 1.处理完主事务.(状态变更为SUCCESS FAIL)
    * 2.处理对应的子事务.(状态变更为PREPARE FAIL)
    * 3.isImmediately TRUE 实时处理子事务commit或rollback
    *
    * @param isImmediately  是否实时处理
    * @param activityStatus 状态 SUCCESS FAIL
    *
    */
  def finishActivity(isImmediately: Boolean, activityStatus: Status.Activity) {
    logger.info("DTSController.finishActivity Paramter{isImmediately=" + isImmediately + ",status=" + activityStatus.value())
    manager.finishActivity(activityStatus, isImmediately)
  }

  /**
    * 开启子事务
    *
    * @param idempotency 幂等id
    * @param name        服务名称
    * @param context     请求参数json
    * @return long
    */
  def startAction(idempotency: String, name: String, context: String): String = {
    logger.info("DTSController.startAction Paramter{idempotency=" + idempotency + ",name=" + name + ",context=" + context)
    if (nameChecker.checkName(name)) {
      manager.startAction(idempotency, name, context)
    }else {
      throw new DTSException(s"Controller start Action is fail, name=${name} => path is not exist")
    }
  }

  /**
    * 子事务处理准备完成
    *
    * @param status prepare
    * @param actionId
    *
    */
  def finishAction(status: ActionStatus, actionId: String) {
    logger.info("DTSController.finishAction Paramter{status=" + status.value() + ",actionId=" + actionId)
    manager.finishAction(status, actionId)
  }
}
