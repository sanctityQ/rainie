package com.itiancai.galaxy.dts.support

import com.alibaba.druid.filter.AutoLoad
import com.itiancai.galaxy.dts.domain.Status
import com.itiancai.galaxy.dts.store.ActionDao
import com.itiancai.galaxy.dts.{TransactionManager, TransactionStatus}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ActionTransactionManager extends TransactionManager {

  @Autowired
  var actionDao:ActionDao = null

  def prepare(actionId: String) = {

    val action = actionDao.findByActionId(actionId)

    actionDao.updateStatus(actionId, Status.ActionStatus.PREPARE, action.getStatus.value())
  }

  override def begin(): Unit = {

  }

  override def rollback(transactionStatus: TransactionStatus): Unit = {
    //rollback-rpc
  }

  override def commit(transactionStatus: TransactionStatus): Unit = ???
}
