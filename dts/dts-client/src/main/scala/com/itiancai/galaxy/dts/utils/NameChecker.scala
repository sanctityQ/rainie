package com.itiancai.galaxy.dts.utils

import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * Created by bao on 16/8/11.
  */

@Component
class NameChecker {
  val logger = LoggerFactory.getLogger(getClass)
  @Autowired
  private val clientFactory: ClientFactory = null

  def checkName(name: String): Future[Boolean] = {
    val (sysName, moduleName, serviceName) = NameResolver.eval(name)
    clientFactory.getPath(sysName, moduleName).map(path =>
      Option(path).isDefined && path.length != 0
    ).handle({
      case t: Throwable => {
        logger.error(s"dtsServer not have path,pathkey=recovery.${sysName}.${moduleName} error", t)
        false
      }
    })
  }
}