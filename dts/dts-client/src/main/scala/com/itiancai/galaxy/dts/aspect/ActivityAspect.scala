package com.itiancai.galaxy.dts.aspect

import java.lang.annotation.Annotation
import java.lang.reflect.Method

import com.alibaba.fastjson.JSON
import com.itiancai.galaxy.dts.annotation.Activity
import com.itiancai.galaxy.dts.domain.{IdGenerator, Status}
import com.itiancai.galaxy.dts.{DTSController, ContextsLocal, DTSException}
import com.twitter.util.{Await, Future}
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}
import org.aspectj.lang.{JoinPoint, ProceedingJoinPoint}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class ActivityAspect {

  @Autowired
  val dtsManager: DTSController = null
  private val logger: Logger = LoggerFactory.getLogger(classOf[ActivityAspect])

  /**
    * 定义切入点
    */
  @Pointcut("@annotation(com.itiancai.galaxy.dts.annotation.Activity)")
  def activityAspect {}

  /**
    * 切点处理befor after throw 三种情况
    *
    * @param joinPoint
    * @throws Exception
    */
  @Around("activityAspect()")
  def doAround(joinPoint: ProceedingJoinPoint) {
    val txId = IdGenerator.genTXId.toString
      ContextsLocal.let_txId(txId)({
        var isImmediately: Boolean = true
        try {
          val mapPra:Map[String, String] = getServiceMethodDescription(joinPoint)
          val busIndex: Int = Integer.parseInt(mapPra.get("busIndex").get)
          val businessType: String = mapPra.get("businessType").get
          val timeOut:Int = java.lang.Integer.parseInt(mapPra.get("timeOut").get)
          val args:List[AnyRef] = joinPoint.getArgs.toList
          dtsManager.startActivity(args(busIndex).toString, businessType, timeOut)
          val obj: Object = joinPoint.proceed
          isCheckReturn(obj)
          isImmediately = java.lang.Boolean.parseBoolean(mapPra.get("isImmediately").get)
          dtsManager.finishActivity(isImmediately, Status.Activity.SUCCESS)
        }
        catch {
          case e: Throwable => {
            logger.error(e.getMessage)
            dtsManager.finishActivity(isImmediately, Status.Activity.FAILL)
          }
        }
      })
    ContextsLocal.clear_txId()
  }

  private def isCheckReturn(obj: Object) {
    if (Option(obj).isDefined && obj.getClass.getName.equals(Future.getClass.getName)) {
      if (Await.result((obj.asInstanceOf[Future[Any]])).hashCode != 200) {
        throw new DTSException("finagle return Fail,return=" + JSON.toJSON(obj))
      }
    }
  }

  /**
    * 获取注解中对方法的描述信息及方法信息
    *
    * @param joinPoint 切点
    * @return Map<String,Object></>
    * @throws Exception
    */
  private def getServiceMethodDescription(joinPoint: JoinPoint): Map[String, String] = {
    val targetName: String = joinPoint.getTarget.getClass.getName
    val methodName: String = joinPoint.getSignature.getName
    val arguments:List[Object] = joinPoint.getArgs.map(ob =>{ob}).toList
    val targetClass: Class[_] = Class.forName(targetName)
    val methods: Array[Method] = targetClass.getMethods
    var map = Map[String, String]()
    for (method <- methods) {
      if (method.getName == methodName) {
        val clazzs: Array[Class[_]] = method.getParameterTypes
        if (clazzs.length == arguments.length) {
          val params = method.getParameterAnnotations
          if(Option(params).isDefined && params.length !=0){
            var num = 0
            for (an:Array[Annotation] <- params){
                if(Option(an).isDefined && !an.isEmpty ){
                  map += ("busIndex" -> num.toString )
                }
             num = num + 1
            }
          }
          if(!map.contains("busIndex")){
            throw new DTSException("activity The method must specify business parameters or idempotent")
          }
          val businessType: String = method.getAnnotation(classOf[Activity]).businessType()
          map += ("businessType" -> businessType)
          if (businessType == null || businessType.toString.length == 0) {
            throw new DTSException("activity fail,because businessType is empty,methodName=" + methodName)
          }
          val isImmediately: String = method.getAnnotation(classOf[Activity]).isImmediately.toString
          map +=("isImmediately" -> isImmediately)
          val timeOut: String = method.getAnnotation(classOf[Activity]).timeOut.toString
          map += ("timeOut" -> timeOut)
        }
      }
    }
    logger.info("activity start. methodName=" + methodName + ",Activity=" + map.toString())
    map
  }
}
