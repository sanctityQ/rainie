package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method

import com.itiancai.galaxy.dts._
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.interceptor.annotation.{ParseActivityAnnotation, ParseActionAnnotation}
import com.twitter.util.Future
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

//@Aspect
//@Component
class ActivityAspect {

  val futureClassName: String = "com.twitter.util.Future"

  @Autowired
  val controller: DTSController = null

  private val logger: Logger = LoggerFactory.getLogger(classOf[ActivityAspect])


  /**
    * 定义切入点
    */
  @Pointcut("@annotation(com.itiancai.galaxy.dts.interceptor.annotation.Activity)")
  def activityAspect {}

  /**
    * 切点处理befor after throw 三种情况
    *
    * @param joinPoint
    * @throws Exception
    */
  @Around("activityAspect()")
  def doAround(joinPoint: ProceedingJoinPoint): Object = {
    val method: Method = joinPoint.getSignature().asInstanceOf[MethodSignature].getMethod

    val parseActivity = ParseActivityAnnotation(method)

    val businessId = parseActivity.businessId(joinPoint.getArgs)

    //事务开启
    logger.info("activity start methodArgs=" + JsonUtil.toJson(JsonUtil.toJson(joinPoint.getArgs)
      + ",businessType=" + parseActivity.businessType
      + ",timeOut=" + parseActivity.timeOut
      + ",bizId" + businessId))
    val txId = controller.startActivity(businessId, parseActivity.businessType, parseActivity.timeOut)

    TXIdLocal.let_txId(txId) {
      handleActivityTransaction(parseActivity.isImmediately, txId, joinPoint)

    }

  }

  /**
    * 事务协调处理
//    *
//    * @param isImmediately
//    * @param txId
//    * @param joinPoint
//    */
//  private[this] def handleActivityTransaction(isImmediately: Boolean, txId: String, joinPoint: ProceedingJoinPoint): Object = {
//    try {
//      handlerFutureResult(joinPoint.proceed, isImmediately).asInstanceOf[Object]
//    } catch {
//      case e: Throwable => {
//        controller.finishActivity(isImmediately, Status.Activity.FAIL)
//        throw e
//      }
//    } finally {
//      TXIdLocal.clear_txId
//    }
//
//  }
//
//  private[this] def handlerFutureResult(obj: Any, isImmediately: Boolean): Any = {
//    if (Option(obj).isDefined && obj.getClass.getSuperclass.getName.equals(futureClassName)) {
//      obj.asInstanceOf[Future[Any]].onSuccess(obj => {
//        controller.finishActivity(isImmediately, Status.Activity.SUCCESS)
//      })
//      obj.asInstanceOf[Future[Any]].onFailure(obj => {
//        controller.finishActivity(isImmediately, Status.Activity.FAIL)
//      })
//    } else {
//      controller.finishActivity(isImmediately, Status.Activity.SUCCESS)
//    }
//    obj
//  }
}

//@Aspect
//@Component
class ActionAspect {
//  @Autowired
//  val controller: DTSController = null
//
//  private val logger: Logger = LoggerFactory.getLogger(classOf[ActionAspect])
//
//  /**
//    * 定义切入点
//    */
//  @Pointcut("@annotation(com.itiancai.galaxy.dts.interceptor.annotation.Action)")
//  def actionAspect {}
//
//  /**
//    * 切点处理befor after throws 三种情况
//    *
//    * @param joinPoint
//    * @throws Exception
//    */
//  @Around("actionAspect()")
//  def doAround(joinPoint: ProceedingJoinPoint): Object = {
//
//    val method: Method = joinPoint.getSignature().asInstanceOf[MethodSignature].getMethod
//
//    val parseAction = ParseActionAnnotation(method)
//
//    val args = joinPoint.getArgs()
//
//    val idempotency = parseAction.instructionId(args)
//
//    logger.info("activity start methodArgs=" + JsonUtil.toJson(JsonUtil.toJson(args)
//      + ",name=" + parseAction.name
//      + ",idempotency=" + idempotency))
//    val actionId = controller.startAction(idempotency, parseAction.name, JsonUtil.toJson(args))
//    handleActionTransaction(actionId, joinPoint)
//
//  }
//
//  /**
//    * 子事务协调
//    *
//    * @param actionId
//    * @param joinPoint
//    */
//  private def handleActionTransaction(actionId: String, joinPoint: ProceedingJoinPoint): Object = {
//    var obj: Object = null
//    try {
//      obj = joinPoint.proceed
//      if (Option(obj).isDefined && obj.getClass.getSuperclass.getName.equals("com.twitter.util.Future")) {
//        obj.asInstanceOf[Future[AnyRef]].onSuccess(obj => {
//          controller.finishAction(Status.ActionStatus.PREPARE, actionId)
//        })
//        obj.asInstanceOf[Future[AnyRef]].onFailure(obj => {
//          controller.finishAction(Status.ActionStatus.FAIL, actionId)
//        })
//      } else {
//        controller.finishAction(Status.ActionStatus.PREPARE, actionId)
//      }
//      obj
//    } catch {
//      case e: Throwable => {
//        controller.finishAction(Status.ActionStatus.FAIL, actionId)
//        throw e
//      }
//    }
//  }

}