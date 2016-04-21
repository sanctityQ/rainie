package com.itiancai.galaxy.thrift.filter

import javax.inject.Singleton

import com.itiancai.galaxy.thrift.{ThriftFilter, ThriftRequest}
import com.twitter.finagle.Service
import com.twitter.util.Future
import org.slf4j.MDC

/**
 * Note: Any MDC filter must be used on conjunction with the LoggingMDCFilter
 * to ensure that diagnostic context is properly managed.
 */
@Singleton
class ThriftMDCFilter extends ThriftFilter{

  override def apply[T, U](request: ThriftRequest[T], service: Service[ThriftRequest[T], U]): Future[U] = {
    MDC.put("method", request.methodName)

    for (id <- request.clientId) {
      MDC.put("clientId", id.name)
    }

    service(request)
  }
}
