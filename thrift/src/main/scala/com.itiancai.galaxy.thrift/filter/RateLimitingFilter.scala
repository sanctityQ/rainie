package com.itiancai.galaxy.thrift.filter

import javax.annotation.PostConstruct
import javax.inject.Inject

import com.itiancai.galaxy.inject.Logging
import com.itiancai.galaxy.thrift.{ThriftRequest, ThriftFilter}
import com.twitter.app.Flags
import com.twitter.finagle.tracing.TraceId
import com.twitter.finagle.thrift.ClientId
import com.twitter.finagle.{RefusedByRateLimiter, Service}
import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.util.{Time, Duration, Future}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import scala.collection.mutable


/**
  * 限流过滤
  * 在.properties中使用 rateLimit:request.methodName, duration, rate,其中duration按照s为单位
  * 如果出现多个rateLimit:request.methodName1, duration1, rate1;request.methodName2, duration2, rate2
  * 支持通配符写法如对所有方法为: rateLimit:*,duration,rate
  *
  * @param statsReceiver
  */
@Component
class ThriftRateLimitingFilter @Inject()
(statsReceiver: StatsReceiver = NullStatsReceiver)
  extends ThriftFilter with Logging {

  val flag = new Flags(this.getClass.getName)

  private[this] val rateLimitStrategy = mutable.HashMap.empty[String, (Duration, Int)]

  /**
    * rateLimit.methodName
    */
  @Value("${rateLimit}")
  private val rateLimit: String = null

  @PostConstruct
  def init: Unit = {
    info("RateLimitingFilter get rateLimitStrategy %s".format(rateLimit))
    for (method <- rateLimit.trim.split(";")) {
      method.split(",") match {
        case Array(methodName, duration, rate) => rateLimitStrategy += ((methodName, (Duration.fromSeconds(duration.toInt), rate.toInt)))
        case _ => throw new RuntimeException("rateLimitStrategy config is error, please config like: methodName, duration, rate;")
      }
    }
    if (rateLimitStrategy.isEmpty) {
      throw new RuntimeException()
    }
  }

  def categorize(request: LocalThriftRequet) = {
    val value: Option[(Duration, Int)] = rateLimitStrategy.get(request.methodName)
    if (value.nonEmpty) {
      val (duration, rate) = value.get
      (request.methodName, duration, rate)
    }
    else {
      val value: Option[(Duration, Int)] = rateLimitStrategy.get("*")

      if (value.isEmpty) {
        throw new RuntimeException()
      }
      val (duration, rate) = value.get
      (request.methodName, duration, rate)

    }
  }

  private[this] val refused = statsReceiver.counter("refused")


  private val strategy = new LocalRateLimitingStrategy(categorize)

  override def apply[T, U](request: ThriftRequest[T], service: Service[ThriftRequest[T], U]): Future[U] = {

    strategy(request) flatMap { isAuthorized =>
      if (isAuthorized)
        service(request)
      else {
        refused.incr()
        Future.exception(new RefusedByRateLimiter)
      }
    }
  }

}

class LocalRateLimitingStrategy(
    categorizer: (LocalThriftRequet => (String, Duration, Int))
  ){


  private[this] val rates = mutable.HashMap.empty[String, (Int, Time)]

  def apply[T](req: ThriftRequest[T]) = synchronized {
    val now = Time.now
    val (id, windowSize, rate) = categorizer(LocalThriftRequet(req.methodName, req.traceId, req.clientId, req.args))
    val (remainingRequests, timestamp) = rates.getOrElse(id, (rate, now))

    val accept = if (timestamp.until(now) > windowSize) {
      rates(id) = (rate, now)
      true
    } else {
      if (remainingRequests > 0) {
        rates(id) = (remainingRequests - 1, timestamp)
        true
      } else {
        false
      }
    }

    Future.value(accept)
  }
}

case class LocalThriftRequet(methodName:String,traceId: TraceId, clientId: Option[ClientId], args: Any)

class RateLimitingFilter[Req](
  strategy: LocalRateLimitingStrategy,
  statsReceiver: StatsReceiver = NullStatsReceiver
  ) extends ThriftFilter {

  private[this] val refused = statsReceiver.counter("refused")

  override def apply[T, U](request: ThriftRequest[T], service: Service[ThriftRequest[T], U]): Future[U] =

    strategy(request) flatMap { isAuthorized =>
      if (isAuthorized)
        service(request)
      else {
        refused.incr()
        Future.exception(new RefusedByRateLimiter)
      }
  }

}


