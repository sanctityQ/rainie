package com.itiancai.galaxy.dts.interceptor

import java.lang.reflect.Method

trait TransactionAttributeSource {

  def getTransactionAttribute(method: Method, targetClass: Class[_]): TransactionAttribute

}
