package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.{TransactionStatus, TransactionManager}


class DefaultTransactionManager extends TransactionManager{

  override def commit(transactionStatus: TransactionStatus) = {
      //

  }

  override def rollback(transactionStatus: TransactionStatus) = {

  }

  override def begin(): Unit = ???
}
