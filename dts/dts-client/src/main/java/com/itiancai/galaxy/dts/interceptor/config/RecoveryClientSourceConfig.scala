package com.itiancai.galaxy.dts.interceptor.config

import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.recovery.{RecoveryClient, RecoverServiceName, RecoveryClientSource}
import com.itiancai.galaxy.dts.thrift.DTSServerApi
import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value

@Component
class RecoveryClientSourceConfig extends RecoveryClientSource{

  @Value("${dts.server.url}")
  private val serverPath: String = null

  private var serverClient: DTSServerApi.FutureIface = null


  @PostConstruct
  def init = {
    serverClient = Thrift.newIface[DTSServerApi.FutureIface](serverPath)
  }

  override def findRecoveryClient(serviceName: RecoverServiceName): RecoveryClient = {
    Await.result(
      serverClient.servicePath(serviceName.systemName, serviceName.moduleName).map(
        path => new RecoveryClient(path))
    )
  }

}
