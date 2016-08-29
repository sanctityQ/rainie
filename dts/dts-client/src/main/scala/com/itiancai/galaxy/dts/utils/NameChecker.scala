package com.itiancai.galaxy.dts.utils

import java.util.concurrent.TimeUnit

import com.itiancai.galaxy.dts.support.ClientFactory
import com.twitter.util.{Duration, Await}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class NameChecker {
  val logger = LoggerFactory.getLogger(getClass)
  @Autowired
  private val clientFactory: ClientFactory = null

  def checkName(name: String): Boolean = {
    val (sysName, moduleName, serviceName) = NameResolver.eval(name)
    val isSuccess = clientFactory.getPath(sysName, moduleName).map(path =>
      Option(path).isDefined && path.length != 0
    ).handle({
      case t: Throwable => {
        logger.error(s"dtsServer not have path,pathkey=recovery.${sysName}.${moduleName} error", t)
        false
      }
    })
    try{
      Await.result(isSuccess,Duration(15,TimeUnit.SECONDS))
    }catch {
      case t: Throwable => {
        logger.error(s"dtsServer http timeout 15s.${sysName}.${moduleName} error", t)
        false
      }
    }
  }
}