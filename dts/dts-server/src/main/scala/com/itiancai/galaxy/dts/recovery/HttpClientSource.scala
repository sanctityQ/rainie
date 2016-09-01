package com.itiancai.galaxy.dts.recovery

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class HttpClientSource extends RecoveryClientSource {

  @Autowired
  val env:Environment = null

  override def findRecoveryClient(recoverServiceName: RecoverServiceName): RecoveryClient = {
    val pathKey = s"recovery.${recoverServiceName.systemName}.${recoverServiceName.moduleName}"
    val path = env.getProperty(pathKey, classOf[String])
    //TODO 判断path是否为null
    new RecoveryClient(path)
  }

}
