package com.itiancai.galaxy.dts.support

import javax.inject.Inject

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.recovery.RecoverServiceName
import com.itiancai.galaxy.dts.{TransactionStatus, TransactionManager}
import com.itiancai.galaxy.dts.domain.{Activity, Status}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component("activityTM")
class ActivityTransactionManager @Inject()
(
  dtsRepository: DTSRepository,
  resourceManager:ResourceManager
)
  extends TransactionManager {

  val logger = LoggerFactory.getLogger(getClass)

  override def commit(transactionStatus: TransactionStatus) = {
    //1.activity status unknown -> success
    dtsRepository.updateActivityStatus(transactionStatus.xId().getGlobalTransactionId, Status.Activity.SUCCESS)

    //2.action status unknown -> success, activity finish 0->2
    if (dtsRepository.lockTX(transactionStatus.xId().getGlobalTransactionId, Status.Activity.SUCCESS)) {
      //3.activity finish 2->1
      val result = Future collect transactionStatus.resouceXids().map( resourceManager.commit)
      result.onSuccess(r=> dtsRepository.finishActivity(transactionStatus.xId().getGlobalTransactionId))
    }
  }

  override def rollback(transactionStatus: TransactionStatus) = {
    //1.activity status unknown -> FAIL
    dtsRepository.updateActivityStatus(transactionStatus.xId().getGlobalTransactionId, Status.Activity.FAIL)
    //2.action status unknown -> FAIL, activity finish 0->2
    if (dtsRepository.lockTX(transactionStatus.xId().getGlobalTransactionId, Status.Activity.FAIL)) {
      //3.activity finish 2->1
      val result = Future collect transactionStatus.resouceXids().map( resourceManager.rollback)
      result.onSuccess(r=> dtsRepository.finishActivity(transactionStatus.xId().getGlobalTransactionId))
    }
  }

  override def begin(attribute: TransactionAttribute): TransactionStatus = {
    val xid = XidFactory.newXid(RecoverServiceName.parse(attribute.name()))

    val activity = new Activity(xid.getGlobalTransactionId, Status.Activity.UNKNOWN, attribute.name,
      attribute.timeOut, attribute.paramValue)
    dtsRepository.saveActivity(activity)

    new DefaultTransactionStatus(xid)
  }
}
