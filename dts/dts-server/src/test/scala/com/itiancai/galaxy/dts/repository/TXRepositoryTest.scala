//package com.itiancai.galaxy.dts.repository
//
//import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
//import com.itiancai.galaxy.dts.domain.Status
//import org.mockito.Mockito._
//import org.scalatest.{BeforeAndAfterEach, WordSpec}
//
///**
//  * Created by bao on 16/8/9.
//  */
//class TXRepositoryTest extends WordSpec with BeforeAndAfterEach {
//
//  val activityDaoM = mock(classOf[ActivityDao])
//
//  val actionDaoM = mock(classOf[ActionDao])
//
//  val txRepository = new TXRepository {
//    override val activityDao = activityDaoM
//    override val actionDao = actionDaoM
//  }
//
//  override protected def beforeEach(): Unit = {
//    reset(activityDaoM, actionDaoM)
//  }
//
//  "synchroActivityStatus" when {
//
//    "updateStatus count = 1" should {
//      "synchro success = true" in {
//        val txId = "txId1"
//        val status = Status.Activity.SUCCESS
//        when(activityDaoM.updateStatus(txId, status.getStatus, Status.Activity.UNKNOWN.getStatus)).thenReturn(1)
//        val flag = txRepository.synchroActivityStatus(txId, status)
//        assert(flag)
//      }
//    }
//
//    "updateStatus count = 0" should {
//      "synchro fail = false" in {
//        val txId = "txId1"
//        val status = Status.Activity.SUCCESS
//        when(activityDaoM.updateStatus(txId, status.getStatus, Status.Activity.UNKNOWN.getStatus)).thenReturn(0)
//        val flag = txRepository.synchroActivityStatus(txId, status)
//        assert(!flag)
//      }
//    }
//  }
//}
