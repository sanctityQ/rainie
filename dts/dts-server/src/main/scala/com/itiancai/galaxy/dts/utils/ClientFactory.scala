package com.itiancai.galaxy.dts.utils

import com.twitter.conversions.time._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class ClientFactory extends RecoveryClientFactory {

  val logger = LoggerFactory.getLogger(getClass)

  private val clientMap = mutable.Map[String, Service[Request, Response]]()

  @Autowired
  val env:Environment = null

  def getClient(sysName: String, moduleName: String): Future[Service[Request, Response]] = {
    //TODO 异常处理
    val pathKey = NameResolver.pathKey(sysName, moduleName)
    val client = clientMap.getOrElse(pathKey, {
      val path = env.getProperty(pathKey, classOf[String])
      logger.info(s"gen client pathKey:${pathKey} path:${path}")
      val client = Http.client.withRequestTimeout(42.seconds).newService(path)
      clientMap += (pathKey -> client)
      client
    })
    Future(client)
  }
}


