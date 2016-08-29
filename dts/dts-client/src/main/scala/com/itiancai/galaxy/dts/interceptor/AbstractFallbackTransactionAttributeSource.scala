package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import org.springframework.core.BridgeMethodResolver
import org.springframework.util.{ClassUtils, ObjectUtils}

import scala.collection.JavaConverters._
import scala.collection.concurrent.Map


abstract class AbstractFallbackTransactionAttributeSource extends TransactionAttributeSource {

  val attributeCache: Map[String, Object] = new ConcurrentHashMap[String, Object](1024).asScala

  protected override def getTransactionAttribute(method: Method, targetClass: Class[_]): TransactionAttribute = {
    null
  }

  protected def getCacheKey(method: Method, targetClass: Class[_]):AnyRef = {
     new DefaultCacheKey(method, targetClass)
  }

  protected  def findTransactionAttribute(specificMethod: Method): TransactionAttribute

  def computeTransactionAttribute(method: Method, targetClass: Class[_]): TransactionAttribute = {
    val userClass: Class[_] = ClassUtils.getUserClass(targetClass)
    val specificMethod: Method = BridgeMethodResolver.findBridgedMethod(
      ClassUtils.getMostSpecificMethod(method, userClass)
    )

    var txAtt: TransactionAttribute = findTransactionAttribute(specificMethod)
    if(txAtt != null){
      txAtt
    }

    if (specificMethod ne method) {
      txAtt = findTransactionAttribute(method)
      if (txAtt != null) {
        return txAtt
      }
    }

    null
  }

  private[this] class DefaultCacheKey(val method: Method,val targetClass: Class[_]) {

    override def equals(other: Any): Boolean = other match {
      case that: DefaultCacheKey => {
        if (other == this) {
          true
        }

        this.method == that.method && ObjectUtils.nullSafeEquals(this.targetClass, that.targetClass)
      }
      case _ => false
    }

    override def hashCode  = {
      this.method.hashCode + (if (this.targetClass != null) this.targetClass.hashCode * 29 else 0);
    }
  }



}


