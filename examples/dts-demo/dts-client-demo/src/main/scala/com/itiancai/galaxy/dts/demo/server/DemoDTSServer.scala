package com.itiancai.galaxy.dts.demo.server

import com.itiancai.galaxy.dts.TransactionHttpServer
import com.itiancai.galaxy.dts.demo.boot.SpringBoot
import com.itiancai.galaxy.thrift.filter._
import com.itiancai.galaxy.thrift.{ThriftRouter, ThriftServer}

object DemoDTSServer extends ThriftServer with TransactionHttpServer{
  addAnnotationClass[SpringBoot]

  override protected def configureThrift(router: ThriftRouter): Unit = {
    router.filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ThriftRateLimitingFilter]
      .add[DemoDTSController]
  }
}
