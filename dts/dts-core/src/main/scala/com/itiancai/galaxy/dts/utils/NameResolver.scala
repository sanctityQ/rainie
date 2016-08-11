package com.itiancai.galaxy.dts.utils

import com.itiancai.galaxy.dts.ClientFactory
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * Created by bao on 16/8/11.
  */
object NameResolver {
  val ACTIVITY_HANDLE_PATH = "/dts/activity"

  val ACTION_HANDLE_PATH = "/dts/action"

  def eval(name: String): (String, String, String) = {
    val array = name.split(":")
    if(array.length != 3) {
      //TODO 异常处理
      throw new RuntimeException(s"resolve name:[${name}] error")
    }
    (array(0), array(1), array(2))
  }

  def pathKey(sysName: String, moduleName: String):String = {
    s"recovery.${sysName}.${moduleName}"
  }

}

@Component
class NameResolver{
  val logger = LoggerFactory.getLogger(getClass)
  @Autowired
  private val clientFactory: ClientFactory = null

  def checkName(name:String):Future[Boolean] ={
    val (sysName,moduleName,serviceName) = NameResolver.eval(name)
    clientFactory.getPath(sysName,moduleName).map(path =>
      Option(path).isDefined && path.length != 0
    ).handle({
      case t: Throwable => {
        logger.error(s"dtsServer not have path,pathkey=recovery.${sysName}.${moduleName} error", t)
        false
      }
    })
  }
}


