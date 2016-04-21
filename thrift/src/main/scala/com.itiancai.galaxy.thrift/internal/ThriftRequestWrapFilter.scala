package com.itiancai.galaxy.thrift.internal

import com.itiancai.galaxy.thrift.ThriftRequest
import com.twitter.finagle.thrift.ClientId
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future

class ThriftRequestWrapFilter[T, U](
  methodName: String)
  extends Filter[T, U, ThriftRequest[T], U] {

  override def apply(request: T, service: Service[ThriftRequest[T], U]): Future[U] = {
    val thriftRequest = new ThriftRequest[T](
      methodName,
      Trace.id,
      ClientId.current,
      request)

    service(thriftRequest)
  }
}