package com.itiancai.galaxy.dts.server

import com.itiancai.galaxy.dts.SpringBoot
import com.itiancai.galaxy.thrift.filter._
import com.itiancai.galaxy.thrift.{ThriftRouter, ThriftServer}

/**
  * Created by bao on 16/8/2.
  */
object DTSServer extends ThriftServer {

  addAnnotationClass[SpringBoot]

  override protected def configureThrift(router: ThriftRouter): Unit = {
    router.filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ThriftRateLimitingFilter]
      .add[DTSServerController]
  }
}
