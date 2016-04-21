package com.itiancai.galaxy.thrift.filter

import com.itiancai.galaxy.thrift.{ThriftRequest, ThriftFilter}
import com.twitter.finagle.Service
import org.slf4j.{FinagleMDCInitializer, MDC}
import org.springframework.stereotype.Component

/**
 * Initialize the Logback Mapped Diagnostic Context, and clear the MDC after each request
  *
  * @see http://logback.qos.ch/manual/mdc.html
 */
@Component
class LoggingMDCFilter extends ThriftFilter {

  /* Initialize Finagle MDC adapter which overrides the standard Logback one */
  FinagleMDCInitializer.init()


  override def apply[T, Rep](request: ThriftRequest[T], service: Service[ThriftRequest[T], Rep]) = {
    service(request).ensure {
      MDC.clear()
    }
  }

}
