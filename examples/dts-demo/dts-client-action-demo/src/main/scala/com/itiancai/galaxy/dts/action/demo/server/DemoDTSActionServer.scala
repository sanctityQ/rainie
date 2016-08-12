package com.itiancai.galaxy.dts.action.demo.server

import com.itiancai.galaxy.dts.action.demo.boot.SpringBoot
import com.itiancai.galaxy.dts.recovery.RecoveryService
import com.itiancai.galaxy.thrift.filter._
import com.itiancai.galaxy.thrift.{ThriftRouter, ThriftServer}

object DemoDTSActionServer extends ThriftServer with RecoveryService{
  addAnnotationClass[SpringBoot]

  override protected def configureThrift(router: ThriftRouter): Unit = {
    router.filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ThriftRateLimitingFilter]
      .add[DemoDTSActionController]
  }
}
