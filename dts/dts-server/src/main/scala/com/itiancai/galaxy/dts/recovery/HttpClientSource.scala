package com.itiancai.galaxy.dts.recovery

import com.itiancai.galaxy.dts.http.{HttpClientSource => ParentHttpClientSource, HttpClient}
import com.itiancai.galaxy.dts.support.ServiceName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class HttpClientSource extends ParentHttpClientSource {

  @Autowired
  val env:Environment = null

  override def findRecoveryClient(serviceName: ServiceName): HttpClient = {
    val pathKey = s"recovery.${serviceName.systemName}.${serviceName.moduleName}"
    val path = env.getProperty(pathKey, classOf[String])
    //TODO 判断path是否为null
    new HttpClient(path)
  }

}
