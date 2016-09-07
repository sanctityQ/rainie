package com.itiancai.galaxy.dts.config

import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.http.{HttpClientSource, HttpClient}
import com.itiancai.galaxy.dts.support.ServiceName
import com.itiancai.galaxy.dts.server.RecoveryService
import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DtsClientHttpClientSourceConfig extends HttpClientSource{

  @Value("${dts.server.url}")
  private val serverPath: String = null

  private var serverClient: RecoveryService.FutureIface = null


  @PostConstruct
  def init = {
    serverClient = Thrift.newIface[RecoveryService.FutureIface](serverPath)
  }

  override def findRecoveryClient(serviceName: ServiceName): HttpClient = {
    Await.result(
      serverClient.servicePath(serviceName.systemName, serviceName.moduleName).map(
        path => new HttpClient(path))
    )
  }

}
