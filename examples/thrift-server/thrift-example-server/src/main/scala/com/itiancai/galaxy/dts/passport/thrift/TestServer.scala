package com.itiancai.galaxy.dts.passport.thrift

import com.itiancai.galaxy.dts.recovery.RecoveryService
import com.itiancai.galaxy.thrift.filter._
import com.itiancai.galaxy.thrift.{ThriftRouter, ThriftServer}
import com.itiancai.passport.SpringBoot


object TestServer extends ThriftServer with RecoveryService{

  addAnnotationClass[SpringBoot]

  val counter = statsReceiver.counter("login_requests_counter")

  override val defaultThriftServerName = "testServer"

  override def configureThrift(router: ThriftRouter) = {
    router.filter[LoggingMDCFilter]
      .filter[TraceIdMDCFilter]
      .filter[AccessLoggingFilter]
      .filter[StatsFilter]
      .filter[ThriftRateLimitingFilter]
      .add[PassportController]
  }

  override def warmup() {
    super.warmup()
    info("Warm-up done.")

  }

}