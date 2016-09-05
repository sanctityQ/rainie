package com.itiancai.galaxy.dts.config

import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.recovery.{RecoverServiceName, RecoveryClient, RecoveryClientSource}
import com.itiancai.galaxy.dts.server.RecoveryService
import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RecoveryClientSourceConfig extends RecoveryClientSource{

  @Value("${dts.server.url}")
  private val serverPath: String = null

  private var serverClient: RecoveryService.FutureIface = null


  @PostConstruct
  def init = {
    serverClient = Thrift.newIface[RecoveryService.FutureIface](serverPath)
  }

  override def findRecoveryClient(serviceName: RecoverServiceName): RecoveryClient = {
    Await.result(
      serverClient.servicePath(serviceName.systemName, serviceName.moduleName).map(
        path => new RecoveryClient(path))
    )
  }

}
