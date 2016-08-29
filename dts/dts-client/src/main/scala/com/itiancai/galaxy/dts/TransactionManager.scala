package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute

trait TransactionManager {

   def begin(transactionAttribute: TransactionAttribute)

   def commit(transactionStatus: TransactionStatus)

   def rollback(transactionStatus: TransactionStatus)

}
