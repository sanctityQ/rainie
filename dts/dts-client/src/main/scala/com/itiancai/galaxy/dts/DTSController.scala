package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.utils.NameChecker
import com.twitter.util.Future
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DTSController {
  @Autowired
  private val manager: DTSServiceManager = null
  @Autowired
  private val nameChecker: NameChecker = null

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  /**
    * 开启主事务,流程:
    * 1.生成id并存放在TreadLocal
    * 2.开启主事务(主事务状态为UNKNOWN)
    *
    * @param bizId   业务id
    * @param typeName    服务名称
    * @param timeOut 超时时间
    */
  def startActivity(bizId: String, typeName: String, timeOut: Int): Future[String] = {
    logger.info("DTSController.startActivity Paramter{bizId=" + bizId + ",typeName=" + typeName + ",timeout=" + timeOut)
    nameChecker.checkName(typeName).map(isHave =>{
      if(isHave){
        manager.startActivity(bizId, typeName, timeOut)
      }else{
        logger.info(s"Controller start Activity is fail, name=${typeName} => path is not exist")
        null
      }
    })
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
    logger.info("DTSController.finishActivity Paramter{isImmediately=" + isImmediately + ",status=" + activityStatus.getStatus)
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
  def startAction(idempotency: String, name: String, context: String): Future[String] = {
    logger.info("DTSController.startAction Paramter{idempotency=" + idempotency + ",name=" + name + ",context=" + context)
    nameChecker.checkName(name).map(isHave =>{
      if(isHave){
        manager.startAction(idempotency, name, context)
      }else{
        logger.info(s"Controller start Action is fail, name=${name} => path is not exist")
        null
      }
    })
  }

  /**
    * 子事务处理准备完成
    *
    * @param status prepare
    * @param actionId
    *
    */
  def finishAction(status: Status.Action, actionId: String) {
    logger.info("DTSController.finishAction Paramter{status=" + status.getStatus + ",actionId=" + actionId)
    manager.finishAction(status, actionId)
  }
}
