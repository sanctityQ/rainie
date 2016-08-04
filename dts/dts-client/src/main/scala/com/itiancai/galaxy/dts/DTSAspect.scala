package com.itiancai.galaxy.dts

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import com.itiancai.galaxy.dts.annotation.{ActionInstruction, Action, Activity, ActivityBusiness}
import com.itiancai.galaxy.dts.domain.{IdGenerator, Status}
import com.itiancai.galaxy.dts.{DTSController, DTSException}
import com.twitter.finagle.context.{LocalContext, Contexts}
import com.twitter.util.Future
import org.apache.commons.beanutils.BeanUtils
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}
import org.aspectj.lang.reflect.MethodSignature
import org.aspectj.lang.{JoinPoint, ProceedingJoinPoint}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

class DTSAspect {

  private val logger: Logger = LoggerFactory.getLogger(classOf[DTSAspect])
  val futureClassName:String = "com.twitter.util.Future"
  case class ActionAnnotation(name: String, index :Int, value: String)
  case class ActivityAnnotation(businessType: String, timeOut: Int, isImmediately: Boolean,
                                index: Int, value: String)

  /**
    * 获取方法/参数注解值
    *
    * @param method
    * @return
    */
  def getAnnotationValue(method: Method, dtsAnName: String):Any ={
    val map = getParamterAnnotationValue(method,dtsAnName)
    getMethodAnnotationValue(method,dtsAnName,map)
  }

  /**
    * 获取方法注解值并组装数据
    *
    * @param method
    * @param dtsAnName
    * @param map
    * @return
    */
  def getMethodAnnotationValue(method: Method,dtsAnName: String,map: Map[String,String]):Any ={
    logger.info("DTSAspect.getMethodAnnotationValue start servceName =" + dtsAnName
                + ",paramterAnnotationMap =" + JsonUtil.toJson(map)
                + ",methodName=" + method.getName)
    val actionMethodAnName = classOf[Action].getName
    val activityMethodAnName = classOf[Activity].getName

    if(dtsAnName.equals(actionMethodAnName)){
      val name: String = method.getAnnotation(classOf[Action]).name
      if (StringUtils.isEmpty(name)) {
        throw new DTSException("action fail,because serverName is empty,methodName=" + method.getName)
      }
      logger.info("DTSAspect.getMethodAnnotationValue end name="+ name + ",methodName=" + method.getName)
      ActionAnnotation(name,Integer.parseInt(map.get("index").get),map.get("value").get)

    }else if(dtsAnName.equals(activityMethodAnName)){
      val businessType: String = method.getAnnotation(classOf[Activity]).businessType()
      if (Option(businessType).isEmpty || businessType.toString.length == 0) {
        throw new DTSException("activity fail,because businessType is empty,methodName=" + method.getName)
      }
      val isImmediately = method.getAnnotation(classOf[Activity]).isImmediately
      val timeOut = method.getAnnotation(classOf[Activity]).timeOut

      logger.info("DTSAspect.getMethodAnnotationValue end businessType="+ businessType
                  + ",isImmediately=" + isImmediately
                  + ",timeOut=" + timeOut
                  + ",methodName=" + method.getName)
      ActivityAnnotation(businessType, timeOut, isImmediately,Integer.parseInt(map.get("index").get), map.get("value").get)

    }
  }

  /**
    * 获取参数注解值(ActionInstruction,ActivityAnnotation)
    *
    * @param method
    * @param dtsAnName
    * @return
    */
  def getParamterAnnotationValue(method: Method,dtsAnName: String):Map[String,String]={
    logger.info("DTSAspect.getParamterAnnotationValue start servceName =" + dtsAnName
      + ",methodName=" + method.getName)

    val params = method.getParameterAnnotations
    if(Option(params).isEmpty || params.isEmpty){
      throw new DTSException("Parameters annotation is empty,methodName=" + method.getName)
    }
    val actionParaAnName = classOf[ActionInstruction].getName
    val activityParaAnName = classOf[ActivityBusiness].getName
    var map = Map[String, String]()
    var num:Int = 0
    for (anArray:Array[Annotation] <- params) {
      if (Option(anArray).isDefined && !anArray.isEmpty){
        for(an <- anArray){
          val anName = an.annotationType().getName
          if((anName.equals(actionParaAnName) && classOf[Action].getName.equals(dtsAnName))
            || (anName.equals(activityParaAnName) && classOf[Activity].getName.equals(dtsAnName))){
            if (map.contains("index")) {
              throw new DTSException("activity parameters @interface Instruction there is only one time,methodName=" + method.getName)
            }
            map += ("index" -> num.toString)
            val value:String = if (anName.equals (activityParaAnName) ) {
              an.asInstanceOf[ActivityBusiness].value ()
            } else {
              an.asInstanceOf[ActionInstruction].value()
            }
            map += ("value" -> value)
          }
        }
      }
      num = num + 1
    }
    if (!map.contains("index")) {
      throw new DTSException("The method must specify business parameters or idempotent,methodName=" + method.getName)
    }
    logger.info("DTSAspect.getParamterAnnotationValue end map =" + JsonUtil.toJson(map) + ",methodName=" + method.getName)
    map
  }
}


@Aspect
@Component
class ActivityAspect extends DTSAspect{
  @Autowired
  val idGenerator: IdGenerator = null
  @Autowired
  val controller: DTSController = null

  private val logger: Logger = LoggerFactory.getLogger(classOf[ActivityAspect])


  /**
    * 定义切入点
    */
  @Pointcut("@annotation(com.itiancai.galaxy.dts.annotation.Activity)")
  def activityAspect{}

  /**
    * 切点处理befor after throw 三种情况
    *
    * @param joinPoint
    * @throws Exception
    */
  @Around("activityAspect()")
  def doAround(joinPoint: ProceedingJoinPoint):Object ={
    val method: Method = joinPoint.getSignature().asInstanceOf[MethodSignature].getMethod
    val txAnValue: ActivityAnnotation = getAnnotationValue(method, classOf[Activity].getName).asInstanceOf[ActivityAnnotation]
    //事务开启
    logger.info("activity start methodArgs=" + JsonUtil.toJson(JsonUtil.toJson(joinPoint.getArgs)
                           + ",businessType="+ txAnValue.businessType
                           + ",timeOut=" + txAnValue.timeOut
                           + ",bizId" + joinPoint.getArgs()(txAnValue.index).toString))
    val txId:String = controller.startActivity(joinPoint.getArgs()(txAnValue.index).toString, txAnValue.businessType, txAnValue.timeOut)
    //事务协调
    handleActivityTransaction(txAnValue.isImmediately, txId, joinPoint)
  }

  /**
    * 事务协调处理
    *
    * @param isImmediately
    * @param txId
    * @param joinPoint
    */
  private def handleActivityTransaction(isImmediately:Boolean, txId: String, joinPoint: ProceedingJoinPoint): Object ={
    TXIdLocal.let_txId(txId)({
      var obj: Object = null
      try{
        obj = joinPoint.proceed
        if (Option(obj).isDefined && obj.getClass.getSuperclass.getName.equals(futureClassName)) {
          obj.asInstanceOf[Future[Any]].onSuccess(obj => {
            controller.finishActivity(isImmediately, Status.Activity.SUCCESS)
          })
          obj.asInstanceOf[Future[Any]].onFailure(obj => {
            controller.finishActivity(isImmediately, Status.Activity.FAIL)
          })
        } else {
          controller.finishActivity(isImmediately, Status.Activity.SUCCESS)
        }
        obj
      }catch {
        case e: Throwable => {
          controller.finishActivity(isImmediately, Status.Activity.FAIL)
          throw e
        }
        obj
      }finally {
        TXIdLocal.clear_txId()
      }
    })
  }
}

@Aspect
@Component
class ActionAspect extends DTSAspect{
  @Autowired
  val controller: DTSController = null

  private val logger: Logger = LoggerFactory.getLogger(classOf[ActionAspect])

  /**
    * 定义切入点
    */
  @Pointcut("@annotation(com.itiancai.galaxy.dts.annotation.Action)")
  def actionAspect {}
  /**
    * 切点处理befor after throws 三种情况
    *
    * @param joinPoint
    * @throws Exception
    */
  @Around("actionAspect()")
  def doAround(joinPoint: ProceedingJoinPoint):Object ={
    val method: Method = joinPoint.getSignature().asInstanceOf[MethodSignature].getMethod
    val actionAnValue: ActionAnnotation = getAnnotationValue(method,classOf[Action].getName).asInstanceOf[ActionAnnotation]
    //子事务开启
    val args = joinPoint.getArgs()
    logger.info("activity start methodArgs="+ JsonUtil.toJson(JsonUtil.toJson(args)
                                            + ",name="+ actionAnValue.name
                                            + ",idempotency=" + args(actionAnValue.index).toString))
    val actionId:String = controller.startAction(args(actionAnValue.index).toString, actionAnValue.name, JsonUtil.toJson(args))
    //子事务协调
    handleActionTransaction(actionId,joinPoint)
  }

  /**
    *  子事务协调
    *
    * @param actionId
    * @param joinPoint
    */
  private def handleActionTransaction(actionId: String, joinPoint: ProceedingJoinPoint):Object ={
    var obj:Object = null
    try{
      obj = joinPoint.proceed
      if (Option(obj).isDefined && obj.getClass.getSuperclass.getName.equals("com.twitter.util.Future")) {
        obj.asInstanceOf[Future[AnyRef]].onSuccess(obj => {
          controller.finishAction(Status.Action.PREPARE, actionId)
        })
        obj.asInstanceOf[Future[AnyRef]].onFailure(obj => {
          controller.finishAction(Status.Action.FAIL, actionId)
        })
      } else {
        controller.finishAction(Status.Action.PREPARE, actionId)
      }
      obj
    }catch {
      case e: Throwable => {
        controller.finishAction(Status.Action.FAIL, actionId)
        throw e
      }
    obj
    }
  }

}
