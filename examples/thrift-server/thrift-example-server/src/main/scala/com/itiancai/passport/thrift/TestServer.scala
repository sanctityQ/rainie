package com.itiancai.passport.thrift

import com.itiancai.galaxy.inject.Logging
import com.itiancai.galaxy.thrift.filter._
import com.itiancai.galaxy.thrift.{ThriftRouter, ThriftServer}
import com.itiancai.passport.SpringBoot


object TestServer extends ThriftServer{

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
    info("Warm-up done.")

  }

}