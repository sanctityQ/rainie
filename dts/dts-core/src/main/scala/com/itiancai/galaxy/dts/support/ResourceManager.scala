package com.itiancai.galaxy.dts.support

import javax.inject.Inject

import com.itiancai.galaxy.dts.domain.{Action, Status}
import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.recovery.{ActionRequest, RecoveryClientSource}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.{XAResource, Xid}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component("actionTM")
class ResourceManager @Inject()
(
  dtsRepository: DTSRepository,
  actionClientSource: RecoveryClientSource
) extends XAResource {

  val logger = LoggerFactory.getLogger(getClass)

  def prepare(xid: Xid) = {
    dtsRepository.prepareAction(xid.getBranchId)
  }

  def begin(xid: Xid, attribute: TransactionAttribute) = {
    val action = new Action(xid.getGlobalTransactionId, xid.getBranchId, Status.Action.UNKNOWN,
      attribute.name.toString, attribute.paramValue())
    dtsRepository.saveAction(action)

  }

  def commit(xid: Xid): Future[Unit] = {
    finishAction(dtsRepository.findAction(xid.getBranchId), Status.Action.SUCCESS)
  }

  def rollback(xid: Xid): Future[Unit] = {
    finishAction(dtsRepository.findAction(xid.getBranchId), Status.Action.FAIL)
  }


  private[this]  def finishAction(action: Action, status: Status.Action): Future[Unit] = {
    if (action.getStatus == status.getStatus) {
      return Future.Unit
    }

    val serviceName = ServiceName(action.getServiceName)

    val method = if (status == Status.Action.SUCCESS) "commit" else "rollback" //提交 | 回滚

    actionClientSource.getTransactionClient(serviceName)
      .request(ActionRequest(serviceName.serviceName, method, action.getInstructionId)).map(flag => {
      if (flag) {
        dtsRepository.finishAction(action.getActionId, status)
      } else {//TODO 定义异常
        throw new RuntimeException("finishAction error")
      }
    })
  }

}
