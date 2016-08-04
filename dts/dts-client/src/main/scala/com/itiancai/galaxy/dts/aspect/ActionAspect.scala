package com.itiancai.galaxy.dts.aspect

import java.lang.annotation.Annotation
import java.lang.reflect.Method

import com.alibaba.fastjson.JSON
import com.itiancai.galaxy.dts.annotation.Action
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.{DTSController, DTSException}
import com.twitter.util.{Await, Future}
import org.aspectj.lang.annotation.{Around, Aspect, Pointcut}
import org.aspectj.lang.{JoinPoint, ProceedingJoinPoint}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
  * Created by lsp on 16/8/2.
  */
@Aspect
@Component
class ActionAspect {

  @Autowired
  val dtsManager: DTSController = null
  private val logger: Logger = LoggerFactory.getLogger(classOf[ActivityAspect])

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
  def doAround(joinPoint: ProceedingJoinPoint) {
    var actionId = 0L
    try {
      val mapPra: Map[String, String] = getServiceMethodDescription(joinPoint)
      val name: String = mapPra.get("name").get
      val instructionIndex: Int = Integer.parseInt(mapPra.get("instructionIdIndex").get)
      val args:List[AnyRef] = joinPoint.getArgs.toList
      val context =args.toString().replaceAll("\n","").replaceAll("\t","")
      actionId = dtsManager.startAction(args(instructionIndex).toString, name,context)
      val obj = joinPoint.proceed
      isCheckReturn(obj)
      dtsManager.finishAction(Status.Action.PREPARE,actionId)
    }
    catch {
      case e: Throwable => {
        logger.error(e.getMessage)
        if(actionId != 0L) dtsManager.finishAction(Status.Action.FAILL,actionId)
      }
    }
  }

  private def isCheckReturn(obj: Object) {
    if (obj.getClass.getName.equals(Future.getClass.getName)) {
      if (Await.result((obj.asInstanceOf[Future[Any]])).hashCode() != 200) {
        throw new DTSException("finagle return Fail,return")
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
    val arguments = joinPoint.getArgs
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
              if(Option(an).isDefined && !an.isEmpty){
                map += ("instructionIdIndex" -> num.toString )
              }
              num = num + 1
            }
          }
          if(!map.contains("instructionIdIndex")){
            throw new DTSException("action The method must specify business parameters or idempotent")
          }
          val name: String = method.getAnnotation(classOf[Action]).name
          if (StringUtils.isEmpty(name)) {
            throw new DTSException("action fail,because name is empty,methodName=" + methodName)
          }
          map += ("name" -> name)
        }
      }
    }
    logger.info("action start. methodName=" + methodName + ",action=" + JSON.toJSONString(map,true))
    map
  }
}
