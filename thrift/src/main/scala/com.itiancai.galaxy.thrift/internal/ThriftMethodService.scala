package com.itiancai.galaxy.thrift.internal

import com.itiancai.galaxy.thrift.ThriftFilter
import com.twitter.finagle.{Filter, Service}
import com.twitter.scrooge.ThriftMethod
import com.twitter.util.Future

class ThriftMethodService[Args, Result](
  val method: ThriftMethod,
  val svc: Service[Args, Result])
  extends Service[Args, Result] {

  private[this] var filter: Filter[Args, Result, Args, Result] = Filter.identity

  def name = method.name

  override def apply(request: Args): Future[Result] = {
    filter.andThen(svc)(request)
  }

  def setFilter(f: ThriftFilter): Unit = {
    filter = new ThriftRequestWrapFilter[Args, Result](method.name)
      .andThen(f.toFilter[Args, Result])
      .andThen(new ThriftRequestUnwrapFilter[Args, Result])
  }
}
