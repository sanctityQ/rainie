package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.{TransactionManager, TransactionStatus}

trait ActionTransactionManager extends TransactionManager {
  def prepare(transactionStatus: TransactionStatus)
}
