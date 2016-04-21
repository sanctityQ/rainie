package com.itiancai.galaxy.thrift.filter

import com.itiancai.galaxy.thrift.{ThriftRequest, ThriftFilter}
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.Service
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class TraceIdMDCFilter extends ThriftFilter{

  override def apply[T, Rep](request: ThriftRequest[T], service: Service[ThriftRequest[T], Rep]) = {
    MDC.put("traceId", Trace.id.traceId.toString())
    service(request)
  }
}
