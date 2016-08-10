package com.itiancai.galaxy.dts.utils

import com.twitter.conversions.time._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import scala.collection.mutable

/**
  * Created by bao on 16/8/4.
  */
@Component
class ClientFactory {

  val logger = LoggerFactory.getLogger(getClass)

  private val clientMap = mutable.Map[String, Service[Request, Response]]()

  @Autowired
  val env:Environment = null

  def getHttpClient(pathKey: String): Service[Request, Response] = {
    clientMap.getOrElse(pathKey, {
      val path = env.getProperty(pathKey, classOf[String])
      logger.info(s"gen client pathKey:${pathKey} path:${path}")
      val client = Http.client.withRequestTimeout(42.seconds).newService(path)
      clientMap += (pathKey -> client)
      client
    })
  }

}

object NameResolver {

  val ACTIVITY_HANDLE_PATH = "/dts/activity"

  val ACTION_HANDLE_PATH = "/dts/action"

  def eval(name: String): (String, String, String) = {
    val array = name.split(":")
    if(array.length != 3) {
      //TODO 异常处理
      throw new NameResolveException
    }
    val sysName = s"${array(0)}-${array(1)}"
    val serviceName = array(2)
    (array(0), array(1), array(2))
  }

  def pathKey(sysName: String, moduleName: String):String = {
    s"recovery.${sysName}.${moduleName}"
  }

}
