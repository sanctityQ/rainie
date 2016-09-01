package com.itiancai.galaxy.dts.support

import javax.inject.Inject

import com.itiancai.galaxy.dts.interceptor.{ExtendTransactionAttribute, TransactionAttribute}
import com.itiancai.galaxy.dts.{TransactionStatus, TransactionManager}
import com.itiancai.galaxy.dts.domain.{Activity, IDFactory, Status}
import com.itiancai.galaxy.dts.repository.DTSRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class ActivityTransactionManager @Inject()
(
  dtsRepository: DTSRepository,
  actionTransManager:ActionTransactionManager,
  idFactory: IDFactory
)
  extends TransactionManager {

  val logger = LoggerFactory.getLogger(getClass)

  override def commit(transactionStatus: TransactionStatus) = {
    //1.activity status unknown -> success
    dtsRepository.updateActivityStatus(transactionStatus.txId(), Status.Activity.SUCCESS)
    //2.action status unknown -> success, activity finish 0->2
    if (dtsRepository.lockTX(transactionStatus.txId(), Status.Activity.SUCCESS)) {
      //3.activity finish 2->1
      actionTransManager.commit(transactionStatus)(dtsRepository.finishActivity)
    }
  }

  override def rollback(transactionStatus: TransactionStatus) = {
    //1.activity status unknown -> FAIL
    dtsRepository.updateActivityStatus(transactionStatus.txId(), Status.Activity.FAIL)
    //2.action status unknown -> FAIL, activity finish 0->2
    if (dtsRepository.lockTX(transactionStatus.txId(), Status.Activity.FAIL)) {
      //3.activity finish 2->1
      actionTransManager.rollback(transactionStatus)(dtsRepository.finishActivity)
    }
  }

  override def begin(attribute: TransactionAttribute): TransactionStatus = {
    val extendAttribute = attribute.asInstanceOf[ExtendTransactionAttribute]
    val txId_ = idFactory.generateTxId(attribute.name())
    val activity = new Activity(txId_, Status.Activity.UNKNOWN, attribute.name,
      extendAttribute.timeOut_(), attribute.paramValue())
    dtsRepository.saveActivity(activity)
    new TransactionStatus() {
      override def txId(): String = txId_
    }
  }
}
