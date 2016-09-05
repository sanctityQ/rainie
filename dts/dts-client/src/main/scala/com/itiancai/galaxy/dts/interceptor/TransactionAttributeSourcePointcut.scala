package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method

import com.itiancai.galaxy.dts.recovery.RecoveryClientSource
import org.springframework.aop.support.StaticMethodMatcherPointcut

abstract class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut with Serializable {

  override def matches(method: Method, targetClass: Class[_]): Boolean = {

    val tas: TransactionAttributeSource = getTransactionAttributeSource

    if (tas == null)
      return false

    val attribute = tas.getTransactionAttribute(method, targetClass)

    if(attribute == null)
      return false
    //初始化client
    getRecoveryClientSource.getTransactionClient(attribute.name())
    return (tas == null || tas.getTransactionAttribute(method, targetClass) != null)

  }

  def getTransactionAttributeSource: TransactionAttributeSource

  def getRecoveryClientSource: RecoveryClientSource
}
