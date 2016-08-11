package com.itiancai.galaxy.dts.utils

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

/**
  * Created by bao on 16/8/11.
  */
trait RecoveryClientFactory {

  def getClient(sysName: String, moduleName: String): Future[Service[Request, Response]]
}
