package com.itiancai.galaxy.dts

trait TransactionManager {

   def begin()

   def commit(transactionStatus: TransactionStatus)

   def rollback(transactionStatus: TransactionStatus)

}
