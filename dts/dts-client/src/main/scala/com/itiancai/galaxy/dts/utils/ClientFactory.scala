package com.itiancai.galaxy.dts.utils

import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.thrift.DTSServerApi
import com.twitter.conversions.time._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service, Thrift}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class ClientFactory extends RecoveryClientFactory {
  val logger = LoggerFactory.getLogger(getClass)
  @Value("${dts.server.url}")
  private val serverPath: String = null

  private val clientMap = mutable.Map[String, Future[Service[Request, Response]]]()

  private val pathMap = mutable.Map[String, String]()

  private var serverClient: DTSServerApi.FutureIface = null

  @PostConstruct
  def init(): Unit = {
    serverClient = Thrift.newIface[DTSServerApi.FutureIface](serverPath)
  }

  override def getClient(sysName: String, moduleName: String): Future[Service[Request, Response]] = {
    val pathKey = NameResolver.pathKey(sysName, moduleName)
    clientMap.getOrElse(pathKey, {
      getPath(sysName, moduleName).map(path => {
        val client = Http.client.withRequestTimeout(42.seconds).newService(path)
        clientMap += (pathKey -> Future(client))
        client
      })
    })
  }

  /**
    * 从dtsServer端获取path
    *
    * @param sysName
    * @param moduleName
    * @return
    */
  def getPath(sysName: String, moduleName: String): Future[String] = {
    val pathKey = NameResolver.pathKey(sysName, moduleName)
    if (pathMap.contains(pathKey)) {
      Future(pathMap.get(pathKey).get)
    } else {
      serverClient.servicePath(sysName, moduleName).map(path => {
        if (Option(path).isDefined && path.length != 0)
          pathMap += (pathKey -> path)
        else {
          logger.error(s"dtsServer not have path,pathkey=${pathKey}")
          throw new NameResolveException
        }
        path
      }).handle({
        case t: Throwable => {
          logger.error(s"dtsServer  pathkey:[${pathKey}] error", t)
          throw new NameResolveException
        }
      })
    }
  }


}
