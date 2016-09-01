package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.{LoggerFactory, Logger}
import org.springframework.core.BridgeMethodResolver
import org.springframework.util.{ClassUtils, ObjectUtils}

import scala.collection.JavaConverters._


abstract class AbstractFallbackTransactionAttributeSource extends TransactionAttributeSource {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val attributeCache = new ConcurrentHashMap[Object, TransactionAttribute](1024).asScala

   override def getTransactionAttribute(method: Method, targetClass: Class[_]): TransactionAttribute = {

    val cacheKey: AnyRef = getCacheKey(method, targetClass)
    val cached: Option[Any] = attributeCache.get(cacheKey)

    if (!cached.isEmpty) {
      return cached.asInstanceOf[TransactionAttribute]
    } else {
      val txAtt = computeTransactionAttribute(method, targetClass)
      //TODO ????
      if(txAtt.isDefined) this.attributeCache.put(cacheKey, txAtt.get)
      null
    }

  }

  protected def getCacheKey(method: Method, targetClass: Class[_]): AnyRef = {
    new DefaultCacheKey(method, targetClass)
  }


  def computeTransactionAttribute(method: Method, targetClass: Class[_]): Option[TransactionAttribute] = {
    val userClass: Class[_] = ClassUtils.getUserClass(targetClass)
    val specificMethod: Method = BridgeMethodResolver.findBridgedMethod(
      ClassUtils.getMostSpecificMethod(method, userClass)
    )

    var txAtt  = Option(findTransactionAttribute(specificMethod))
    if (txAtt.isDefined) {
      return txAtt
    }

    if (specificMethod ne method) {
      txAtt = Option(findTransactionAttribute(method))
      if (txAtt.isDefined) {
        return txAtt
      }
    }

    None
  }

  protected def findTransactionAttribute(specificMethod: Method): TransactionAttribute


  private[this] class DefaultCacheKey(val method: Method, val targetClass: Class[_]) {

    def canEqual(other: Any): Boolean = other.isInstanceOf[DefaultCacheKey]

    override def equals(other: Any): Boolean = other match {
      case that: DefaultCacheKey => {

        if (other == this) {
         return true
        }

        (that canEqual this) && (this.method == that.method) &&
            ObjectUtils.nullSafeEquals(this.targetClass, that.targetClass)
      }
      case _ => false
    }

    override def hashCode = {
      this.method.hashCode + (if (this.targetClass != null) this.targetClass.hashCode * 29 else 0);
    }
  }


}


