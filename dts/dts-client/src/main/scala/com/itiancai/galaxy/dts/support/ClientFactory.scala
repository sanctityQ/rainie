package com.itiancai.galaxy.dts.support

import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.server.DTSServerApi
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class ClientFactory {
  val logger = LoggerFactory.getLogger(getClass)

//  @Value("${dts.server.url}")
  private val serverPath: String = null

  private val clientMap = mutable.Map[String, Future[Service[Request, Response]]]()

  private val pathMap = mutable.Map[String, String]()

  private var serverClient: DTSServerAapi.FutureIface = null

  @PostConstruct
  def init(): Unit = {
//    serverClient = Thrift.newIface[DTSServerApi.FutureIface](serverPath)
  }

  def getClient(sysName: String, moduleName: String) = {
//    val pathKey = NameResolver.pathKey(sysName, moduleName)
//    clientMap.getOrElse(pathKey, {
//      getPath(sysName, moduleName).map(path => {
//        val client = Http.client.withRequestTimeout(42.seconds).newService(path)
//        clientMap += (pathKey -> Future(client))
//        client
//      })
//    })
  }

  /**
    * 从dtsServer端获取path
    *
    * @param sysName
    * @param moduleName
    * @return
    */
  def getPath(sysName: String, moduleName: String): Future[String] = {
//    val pathKey = NameResolver.pathKey(sysName, moduleName)
//    Future.value(pathKey)
//    if (pathMap.contains(pathKey)) {
//      Future(pathMap.get(pathKey).get)
//    } else {
//      serverClient.servicePath(sysName, moduleName).map(path => {
//        if (Option(path).isDefined && path.length != 0)
//          pathMap += (pathKey -> path)
//        else {
//          logger.error(s"dtsServer not have path,pathkey=${pathKey}")
//          throw new NameResolveException
//        }
//        path
//      }).handle({
//        case t: Throwable => {
//          logger.error(s"dtsServer  pathkey:[${pathKey}] error", t)
//          throw new NameResolveException
//        }
//      })
//    }
    return Future("")
  }


}
