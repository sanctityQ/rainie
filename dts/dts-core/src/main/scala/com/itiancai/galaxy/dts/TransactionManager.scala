package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute

trait TransactionManager {

  def begin(transactionAttribute: TransactionAttribute): TransactionStatus

  def commit(transactionStatus: TransactionStatus)

  def rollback(transactionStatus: TransactionStatus)

}
