package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.{TransactionManager, TransactionStatus}
import org.springframework.stereotype.Component

@Component
class ActionTransactionManager  {

//  @Autowired
//  var actionDao:ActionDao = null

  def prepare(actionId: String) = {

//    val action = actionDao.findByActionId(actionId)

//    actionDao.updateStatus(actionId, Status.ActionStatus.PREPARE, action.getStatus.value())
  }

   def begin(): Unit = {

  }

   def rollback(transactionStatus: TransactionStatus): Unit = {
    //rollback-rpc
  }

   def commit(transactionStatus: TransactionStatus): Unit = {}
}
