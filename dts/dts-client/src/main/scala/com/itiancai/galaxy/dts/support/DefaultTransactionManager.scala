package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.interceptor.annotation.ActivityAnnotationAttribute
import com.itiancai.galaxy.dts.{TransactionStatus, TransactionManager}


class DefaultTransactionManager extends TransactionManager{

  override def commit[TransactionStatus](transactionStatus: TransactionStatus) = {
      //

  }

  override def rollback[TransactionStatus](transactionStatus: TransactionStatus) = {

  }

  override def begin[T <: TransactionAttribute](transactionAttribute: T): TransactionStatus = {
    transactionAttribute.name()
    null
  }
}
