package com.itiancai.galaxy.dts.recovery

import java.util.concurrent.ConcurrentHashMap

import com.twitter.finagle.Http
import com.twitter.conversions.time._
import com.twitter.finagle.http.{Method, Version, Request}
import com.twitter.util.Future
import scala.collection.JavaConverters._



class RecoveryClient(val path:String) {

  val client = Http.client.withRequestTimeout(5.seconds).newService(path)

  def request(actionRequest: ActionRequest): Future[Boolean] = {
    client(actionRequest.request).map( _.contentString == "true")
  }

  def request(activityStatusRequest: ActivityStatusRequest): Future[Int] = {
    client(activityStatusRequest.request).map( _.contentString.toInt)
  }

}

trait RecoveryClientSource {

  val attributeCache = new ConcurrentHashMap[Object, RecoveryClient](1024).asScala

  def getTransactionClient(recoverServiceName: RecoverServiceName): RecoveryClient = {

    val cached: Option[Any] = attributeCache.get(recoverServiceName)

    if (cached.isDefined) {
      return cached.get.asInstanceOf[RecoveryClient]
    }

    val recoveryClient = findRecoveryClient(recoverServiceName)
    attributeCache.put(recoverServiceName, recoveryClient)
    recoveryClient
  }

  def findRecoveryClient(recoverServiceName: RecoverServiceName): RecoveryClient

}

object RecoverServiceName{

  def parse(name:String): RecoverServiceName ={
    val array = name.split(":")
    if(array.length != 3) {
      throw new RuntimeException(s"resolve name:[${name}] error")
    }
    RecoverServiceName(array(0), array(1), array(2))
  }

}

case class RecoverServiceName(val systemName: String, val moduleName: String, val serviceName: String)  {

  def canEqual(other: Any): Boolean = other.isInstanceOf[RecoverServiceName]

  override def equals(other: Any): Boolean = other match {
    case that: RecoverServiceName =>
      (that canEqual this) &&
        systemName == that.systemName &&
        moduleName == that.moduleName
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(systemName, moduleName)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

case class ActionRequest(val name: String, val actionMethod : String, val instructionId: String) {

  val path = s"/dts/action?id=${instructionId}&name=${name}&method=${actionMethod}"

  val request = Request(Version.Http11, Method.Get, path)

}

case class ActivityStatusRequest(val businessType: String, val businessId: String)  {

  val path = s"/dts/activity?businessId=${businessId}&businessType=${businessType}"

  val request = Request(Version.Http11, Method.Get, path)
}



