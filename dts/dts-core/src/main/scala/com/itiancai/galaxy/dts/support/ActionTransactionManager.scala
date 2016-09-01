package com.itiancai.galaxy.dts.support

import javax.inject.Inject

import com.itiancai.galaxy.dts.domain.{Action, IDFactory, Status}
import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.recovery.{ActionRequest, RecoverServiceName, RecoveryClientSource}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.{TXIdLocal, TransactionStatus}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component("actionTM")
class ActionTransactionManager @Inject()
(
  dtsRepository: DTSRepository,
  actionClientSource: RecoveryClientSource,
  idFactory: IDFactory
) {

  val logger = LoggerFactory.getLogger(getClass)

  def prepare(actionId: String) = {
    dtsRepository.prepareAction(actionId)
  }

  //TODO TXIDLocal的问题,begin只有主事务使用
  def begin(attribute: TransactionAttribute): TransactionStatus = {
    val actionId = idFactory.getActionId(attribute.name)
    val action = new Action(TXIdLocal.current_txId(), actionId, Status.Action.UNKNOWN,
      attribute.name, attribute.paramValue())
    dtsRepository.saveAction(action)
    new TransactionStatus() {
      override def txId(): String = actionId
    }
  }

  def commit(transactionStatus: TransactionStatus)(finish:String => Unit) {
    finishActions(transactionStatus.txId(), Status.Action.SUCCESS).onSuccess(flag =>{
      if(flag) finish(transactionStatus.txId())
      else logger.warn("The transaction is not completed,because the child has failed transaction")
    })
  }

  def rollback(transactionStatus: TransactionStatus)(finish:String => Unit) {
    finishActions(transactionStatus.txId(), Status.Action.FAIL).onSuccess(flag =>{
      if(flag) finish(transactionStatus.txId())
      else logger.warn("The transaction is not completed,because the child has failed transaction")
    })
  }

  /**
    * 完成子事务(提交或回滚)
    *
    * @param txId
    * @param status
    * @return
    */
  private[this] def finishActions(txId: String, status: Status.Action):Future[Boolean] = {
    def finishAction(action: Action): Future[Boolean] = {
      if (action.getStatus == status.getStatus) {
       return Future(true)
      }

      val recoverServiceName = RecoverServiceName.parse(action.getServiceName)

      val method = if (status == Status.Action.SUCCESS) "commit" else "rollback" //提交 | 回滚

      actionClientSource.getTransactionClient(recoverServiceName)
        .request(ActionRequest(recoverServiceName.serviceName, method, action.getInstructionId)).map(flag => {
        if (flag) {
          dtsRepository.finishAction(action.getActionId, status)
        }
        flag
      })
    }
    //action prepare -> commit/rollback
    val finishList_f = Future collect dtsRepository.listActionByTxId(txId).map(finishAction)

    finishList_f.map(!_.contains(false))

  }
}
