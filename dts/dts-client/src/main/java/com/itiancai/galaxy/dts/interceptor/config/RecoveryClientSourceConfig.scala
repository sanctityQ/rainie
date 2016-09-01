package com.itiancai.galaxy.dts.interceptor.config

import com.itiancai.galaxy.dts.recovery.{RecoveryClient, RecoverServiceName, RecoveryClientSource}
import org.springframework.stereotype.Component

@Component
class RecoveryClientSourceConfig extends RecoveryClientSource{
  override def findRecoveryClient(recoverServiceName: RecoverServiceName): RecoveryClient = {
    null
  }
}
