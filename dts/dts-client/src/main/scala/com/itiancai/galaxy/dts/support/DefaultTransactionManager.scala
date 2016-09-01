package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.interceptor.annotation.ActivityAnnotationAttribute
import com.itiancai.galaxy.dts.{TransactionStatus, TransactionManager}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DefaultTransactionManager extends TransactionManager{


  override def begin(transactionAttribute: TransactionAttribute): TransactionStatus = {

    null
  }

  override def rollback(transactionStatus: TransactionStatus): Unit = {}

  override def commit(transactionStatus: TransactionStatus): Unit = {}
}
