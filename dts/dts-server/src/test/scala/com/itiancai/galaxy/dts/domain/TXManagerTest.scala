package com.itiancai.galaxy.dts.domain

import com.itiancai.galaxy.dts.DTSServiceManager
import com.itiancai.galaxy.dts.dao.ActivityDao
import com.itiancai.galaxy.dts.repository.{DTSRepository, TXRepository}
import com.itiancai.galaxy.dts.utils.{RecoveryClientFactory, RedisService, SynchroException}
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status => HttpStatus}
import com.twitter.util.{Await, Future}
import org.mockito.ArgumentMatcher
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

/**
  * Created by bao on 16/8/8.
  */
class TXManagerTest extends WordSpec with BeforeAndAfterEach {

  val redisServiceM = mock(classOf[RedisService])

  val txRepositoryM = mock(classOf[TXRepository])

  val clientM: Service[Request, Response] = mock(classOf[Service[Request, Response]])

  val coreManagerM = mock(classOf[DTSServiceManager])

  val coreRepositoryM = mock(classOf[DTSRepository])

  val activityDaoM = mock(classOf[ActivityDao])

  val txManager = new TXManager {
    override val activityDao = activityDaoM
    override val coreRepository = coreRepositoryM
    override val coreManager = coreManagerM
    override val txRepository = txRepositoryM
    override val clientFactory = new RecoveryClientFactory {
        override def getClient(sysName: String, moduleName: String): Future[Service[Request, Response]] = {
          Future(clientM)
        }
      }
  }

  override protected def beforeEach(): Unit = {
    reset(txRepositoryM, clientM, redisServiceM, coreManagerM, coreRepositoryM)
  }

  "TXManager-synchroActivityStatus" when {
    "status=UNKNOWN & search activity response = 0" should {
      "status = SUCCESS" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(
          clientM.apply(argThat(new ArgumentMatcher[Request] {
            override def matches(argument: scala.Any): Boolean = {
              val requet = argument.asInstanceOf[Request]
              val businessId = requet.getParam("businessId")
              val serviceName = requet.params("businessType")
              businessId == activity.getBusinessId && serviceName == "invest"
            }
          }))
        ).thenReturn({
          val response = Response()
          response.setContentString("0")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(true)
        val f = txManager.synchroActivityStatus(activity.getTxId)
        assert(Await.result(f) == Status.Activity.SUCCESS)
      }
    }

    "status=UNKNOWN & search activity response = 1" should {
      "status = FAILL" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val businessId = requet.getParam("businessId")
            val serviceName_o = requet.params.get("businessType")
            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
          }
        }))).thenReturn({
          val response = Response()
          response.setContentString("1")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(true)
        val f = txManager.synchroActivityStatus(activity.getTxId)
        assert(Await.result(f) == Status.Activity.FAIL)
      }
    }

    "status=UNKNOWN & search activity response = 2" should {
      "status = FAILL" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val businessId = requet.getParam("businessId")
            val serviceName_o = requet.params.get("businessType")
            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
          }
        }))).thenReturn({
          val response = Response()
          response.setContentString("2")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(true)
        val f = txManager.synchroActivityStatus(activity.getTxId)
        assert(Await.result(f) == Status.Activity.FAIL)
      }
    }

    "search activity response fail" should {
      "throw SynchroException" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val businessId = requet.getParam("businessId")
            val serviceName_o = requet.params.get("businessType")
            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
          }
        }))).thenReturn({
          val response = Response(HttpStatus.InternalServerError)
          Future(response)
        })
        val f = txManager.synchroActivityStatus(activity.getTxId)
        intercept[SynchroException](Await.result(f))
        verify(coreRepositoryM, never()).updateActivityStatus(org.mockito.Matchers.eq("txId1"), org.mockito.Matchers.any[Status.Activity])
      }
    }

    "synchroActivityStatus return false" should {
      "throw SynchroException" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val businessId = requet.getParam("businessId")
            val serviceName_o = requet.params.get("businessType")
            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
          }
        }))).thenReturn({
          val response = Response()
          response.setContentString("2")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(false)
        val f = txManager.synchroActivityStatus(activity.getTxId)
        intercept[SynchroException](Await.result(f))
      }
    }
  }

  "TXManager-finishActivity" when {

    "synchroActivityStatus-SUCCESS & finishActions true" should {
      "finishActivity" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(
          clientM.apply(argThat(new ArgumentMatcher[Request] {
            override def matches(argument: scala.Any): Boolean = {
              val requet = argument.asInstanceOf[Request]
              val businessId = requet.getParam("businessId")
              val serviceName = requet.params("businessType")
              businessId == activity.getBusinessId && serviceName == "invest"
            }
          }))
        ).thenReturn({
          val response = Response()
          response.setContentString("0")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(true)
        when(coreManagerM.finishActions(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(Future(true))
        txManager.finishActivity(activity.getTxId)
        verify(coreRepositoryM, times(1)).finishActivity(activity.getTxId)
      }
    }

    "synchroActivityStatus-SUCCESS & finishActions false" should {
      "reclaimTX" in {
        val activity = new Activity()
        activity.setId(1L)
        activity.setTxId("txId1")
        activity.setBusinessId("biz_id")
        activity.setBusinessType("p2p:lending:invest")
        activity.setStatus(Status.Activity.UNKNOWN)
        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
        when(
          clientM.apply(argThat(new ArgumentMatcher[Request] {
            override def matches(argument: scala.Any): Boolean = {
              val requet = argument.asInstanceOf[Request]
              val businessId = requet.getParam("businessId")
              val serviceName = requet.params("businessType")
              businessId == activity.getBusinessId && serviceName == "invest"
            }
          }))
        ).thenReturn({
          val response = Response()
          response.setContentString("0")
          Future(response)
        })
        when(coreRepositoryM.updateActivityStatus(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(true)
        when(coreManagerM.finishActions(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(Future(false))
        txManager.finishActivity(activity.getTxId)
        verify(txRepositoryM, times(1)).reclaimTX(activity.getTxId)
      }
    }

  }
}
