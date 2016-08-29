package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method

import org.springframework.aop.support.StaticMethodMatcherPointcut

abstract class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut with Serializable {

  override def matches(method: Method, targetClass: Class[_]): Boolean = {

    val tas: TransactionAttributeSource = getTransactionAttributeSource

    return (tas == null || tas.getTransactionAttribute(method, targetClass) != null)

  }

  def getTransactionAttributeSource: TransactionAttributeSource
}
