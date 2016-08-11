package com.itiancai.galaxy.dts

import com.itiancai.dts.client.SpringBoot
import com.itiancai.galaxy.dts.recovery.RecoveryService

/**
  * Created by ChengQi on 8/8/16.
  */
object TestHttpServer extends RecoveryService{
  addAnnotationClass[SpringBoot]
}
