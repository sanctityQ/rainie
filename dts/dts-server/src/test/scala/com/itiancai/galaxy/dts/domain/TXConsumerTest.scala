//package com.itiancai.galaxy.dts.domain
//
//import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
//import com.itiancai.galaxy.dts.repository.TXRepository
//import com.itiancai.galaxy.dts.utils.{ClientFactory, RedisService, SynchroException}
//import com.twitter.finagle.Service
//import com.twitter.finagle.http.{Request, Response, Status => HttpStatus}
//import com.twitter.util.{Await, Future}
//import org.mockito.ArgumentMatcher
//import org.mockito.Matchers._
//import org.mockito.Mockito._
//import org.scalatest.{BeforeAndAfterEach, WordSpec}
//
//import scala.collection.JavaConversions._
//
///**
//  * Created by bao on 16/8/8.
//  */
//class TXConsumerTest extends WordSpec with BeforeAndAfterEach {
//
//  val redisServiceM = mock(classOf[RedisService])
//
//  val activityDaoM = mock(classOf[ActivityDao])
//
//  val actionDaoM = mock(classOf[ActionDao])
//
//  val txRepositoryM = mock(classOf[TXRepository])
//
//  val clientM: Service[Request, Response] = mock(classOf[Service[Request, Response]])
//
//  val consumerManager = new ConsumerManager {
//    override val activityDao = activityDaoM
//    override val actionDao = actionDaoM
//    override val txRepository = txRepositoryM
//    override val clientFactory = {
//      new ClientFactory {
//        override def getHttpClient(sysName: String): Service[Request, Response] = {
//          clientM
//        }
//      }
//    }
//  }
//
//  val consumerManagerM = mock(classOf[ConsumerManager])
//
//  val txConsumer = new TXConsumer{
//    override val compensateQueue: String = "tx.compensate.queue"
//
//    override val redisService = redisServiceM
//
//    override val txRepository = txRepositoryM
//
//    override val consumerManager = consumerManagerM
//  }
//
//  override protected def beforeEach(): Unit = {
//    reset(activityDaoM, actionDaoM, txRepositoryM, clientM, redisServiceM, consumerManagerM)
//  }
//
//  "ConsumerManager-synchroActivityStatus" when {
//    "status=UNKNOWN & search activity response = 0" should {
//      "status = SUCCESS" in {
//        val activity = new Activity()
//        activity.setId(1L)
//        activity.setTxId("txId1")
//        activity.setBusinessId("biz_id")
//        activity.setBusinessType("p2p:lending:invest")
//        activity.setStatus(Status.Activity.UNKNOWN)
//        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
//        when(
//          clientM.apply(argThat(new ArgumentMatcher[Request] {
//            override def matches(argument: scala.Any): Boolean = {
//              val requet = argument.asInstanceOf[Request]
//              val businessId = requet.getParam("businessId")
//              val serviceName = requet.params("serviceName")
//              businessId == activity.getBusinessId && serviceName == "invest"
//            }
//          }))
//        ).thenReturn({
//          val response = Response()
//          response.setContentString("0")
//          Future(response)
//        })
//        when(txRepositoryM.synchroActivityStatus(activity.getTxId, Status.Activity.SUCCESS)).thenReturn(true)
//        val f = consumerManager.synchroActivityStatus(activity.getTxId)
//        assert(Await.result(f) == Status.Activity.SUCCESS)
//      }
//    }
//
//    "status=UNKNOWN & search activity response = 1" should {
//      "status = FAILL" in {
//        val activity = new Activity()
//        activity.setId(1L)
//        activity.setTxId("txId1")
//        activity.setBusinessId("biz_id")
//        activity.setBusinessType("p2p:lending:invest")
//        activity.setStatus(Status.Activity.UNKNOWN)
//        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val businessId = requet.getParam("businessId")
//            val serviceName_o = requet.params.get("serviceName")
//            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
//          }
//        }))).thenReturn({
//          val response = Response()
//          response.setContentString("1")
//          Future(response)
//        })
//        when(txRepositoryM.synchroActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(true)
//        val f = consumerManager.synchroActivityStatus(activity.getTxId)
//        assert(Await.result(f) == Status.Activity.FAIL)
//      }
//    }
//
//    "status=UNKNOWN & search activity response = 2" should {
//      "status = FAILL" in {
//        val activity = new Activity()
//        activity.setId(1L)
//        activity.setTxId("txId1")
//        activity.setBusinessId("biz_id")
//        activity.setBusinessType("p2p:lending:invest")
//        activity.setStatus(Status.Activity.UNKNOWN)
//        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val businessId = requet.getParam("businessId")
//            val serviceName_o = requet.params.get("serviceName")
//            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
//          }
//        }))).thenReturn({
//          val response = Response()
//          response.setContentString("2")
//          Future(response)
//        })
//        when(txRepositoryM.synchroActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(true)
//        val f = consumerManager.synchroActivityStatus(activity.getTxId)
//        assert(Await.result(f) == Status.Activity.FAIL)
//      }
//    }
//
//    "search activity response fail" should {
//      "throw SynchroException" in {
//        val activity = new Activity()
//        activity.setId(1L)
//        activity.setTxId("txId1")
//        activity.setBusinessId("biz_id")
//        activity.setBusinessType("p2p:lending:invest")
//        activity.setStatus(Status.Activity.UNKNOWN)
//        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val businessId = requet.getParam("businessId")
//            val serviceName_o = requet.params.get("serviceName")
//            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
//          }
//        }))).thenReturn({
//          val response = Response(HttpStatus.InternalServerError)
//          Future(response)
//        })
//        val f = consumerManager.synchroActivityStatus(activity.getTxId)
//        intercept[SynchroException](Await.result(f))
//        verify(txRepositoryM, never()).synchroActivityStatus(org.mockito.Matchers.eq("txId1"), org.mockito.Matchers.any[Status.Activity])
//      }
//    }
//
//    "synchroActivityStatus return false" should {
//      "throw SynchroException" in {
//        val activity = new Activity()
//        activity.setId(1L)
//        activity.setTxId("txId1")
//        activity.setBusinessId("biz_id")
//        activity.setBusinessType("p2p:lending:invest")
//        activity.setStatus(Status.Activity.UNKNOWN)
//        when(activityDaoM.findByTxId("txId1")).thenReturn(activity)
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val businessId = requet.getParam("businessId")
//            val serviceName_o = requet.params.get("serviceName")
//            businessId == activity.getBusinessId && serviceName_o.isDefined && serviceName_o.get == "invest"
//          }
//        }))).thenReturn({
//          val response = Response()
//          response.setContentString("2")
//          Future(response)
//        })
//        when(txRepositoryM.synchroActivityStatus(activity.getTxId, Status.Activity.FAIL)).thenReturn(false)
//        val f = consumerManager.synchroActivityStatus(activity.getTxId)
//        intercept[SynchroException](Await.result(f))
//      }
//    }
//  }
//
//  "ConsumerManager-finishActions" when {
//
//    "TX status = SUCCESS & all action success" should {
//      "result = true" in {
//        val txId = "txId1"
//        val action1 = new Action()
//        action1.setId(1L)
//        action1.setInstructionId("InstructionId1")
//        action1.setActionId("action1")
//        action1.setServiceName("p2p:interact:trigger")
//        action1.setTxId(txId)
//        action1.setStatus(Status.Action.PREPARE)
//        val action2 = new Action()
//        action2.setId(2L)
//        action2.setInstructionId("InstructionId2")
//        action2.setActionId("action2")
//        action2.setServiceName("p2p:user:bindInviteCode")
//        action2.setTxId(txId)
//        action2.setStatus(Status.Action.PREPARE)
//        when(actionDaoM.findByTxId(txId)).thenReturn(List(action1, action2))
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val instructionId = requet.getParam("instructionId")
//            val serviceName = requet.getParam("serviceName")
//            val method = requet.getParam("method")
//            List("InstructionId1", "InstructionId2").contains(instructionId) &&
//              List("trigger", "bindInviteCode").contains(serviceName) &&
//              method == "commit"
//          }
//        }))).thenReturn({
//          val response = Response()
//          response.setContentString("true")
//          Future(response)
//        })
//        when(txRepositoryM.finishAction(argThat(new ArgumentMatcher[String] {
//          override def matches(argument: scala.Any): Boolean = {
//            val actionId = argument.asInstanceOf[String]
//            List("action1", "action2").contains(actionId)
//          }
//        }), org.mockito.Matchers.eq(Status.Action.SUCCESS))).thenReturn(true)
//        val result_f = consumerManager.finishActions(txId, Status.Activity.SUCCESS)
//        assert(Await.result(result_f))
//      }
//    }
//
//    "TX status = SUCCESS & finish action fail" should {
//      "result = false" in {
//        val txId = "txId1"
//        val action1 = new Action()
//        action1.setId(1L)
//        action1.setInstructionId("InstructionId1")
//        action1.setActionId("action1")
//        action1.setServiceName("p2p:interact:trigger")
//        action1.setTxId(txId)
//        action1.setStatus(Status.Action.PREPARE)
//        val action2 = new Action()
//        action2.setId(2L)
//        action2.setInstructionId("InstructionId2")
//        action2.setActionId("action2")
//        action2.setServiceName("p2p:user:bindInviteCode")
//        action2.setTxId(txId)
//        action2.setStatus(Status.Action.PREPARE)
//        when(actionDaoM.findByTxId(txId)).thenReturn(List(action1, action2))
//        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
//          override def matches(argument: scala.Any): Boolean = {
//            val requet = argument.asInstanceOf[Request]
//            val instructionId = requet.getParam("instructionId")
//            val serviceName = requet.getParam("serviceName")
//            val method = requet.getParam("method")
//            List("InstructionId1", "InstructionId2").contains(instructionId) &&
//              List("trigger", "bindInviteCode").contains(serviceName) &&
//              method == "commit"
//          }
//        }))).thenReturn({
//          val response = Response()
//          response.setContentString("false")
//          Future(response)
//        })
//        val result_f = consumerManager.finishActions(txId, Status.Activity.SUCCESS)
//        assert(!Await.result(result_f))
//        verify(txRepositoryM, never()).finishAction(argThat(new ArgumentMatcher[String] {
//          override def matches(argument: scala.Any): Boolean = {
//            val actionId = argument.asInstanceOf[String]
//            List("action1", "action2").contains(actionId)
//          }
//        }), org.mockito.Matchers.eq(Status.Action.SUCCESS))
//      }
//    }
//  }
//
//  "txConsumer-process" when {
//    "tx status = SUCCESS & finish actions success" should {
//      "finishActivity" in {
//        val txId = "tx1"
//        val status = Status.Activity.SUCCESS
//        when(redisServiceM.rpop("tx.compensate.queue")).thenReturn(txId)
//        when(consumerManagerM.synchroActivityStatus(txId)).thenReturn(Future(status))
//        when(consumerManagerM.finishActions(txId, status)).thenReturn(Future(true))
//        txConsumer.process(1)
//        verify(txRepositoryM, times(1)).finishActivity(txId)
//      }
//    }
//
//    "tx status = SUCCESS & finish actions fail" should {
//      "reclaimTX" in {
//        val txId = "tx1"
//        val status = Status.Activity.SUCCESS
//        when(redisServiceM.rpop("tx.compensate.queue")).thenReturn(txId)
//        when(consumerManagerM.synchroActivityStatus(txId)).thenReturn(Future(status))
//        when(consumerManagerM.finishActions(txId, status)).thenReturn(Future(false))
//        txConsumer.process(1)
//        verify(txRepositoryM, times(0)).finishActivity(txId)
//        verify(txRepositoryM, times(1)).reclaimTX(txId)
//      }
//    }
//
//    "synchroActivityStatus fail" should {
//      "reclaimTX" in {
//        val txId = "tx1"
//        val status = Status.Activity.SUCCESS
//        when(redisServiceM.rpop("tx.compensate.queue")).thenReturn(txId)
//        when(consumerManagerM.synchroActivityStatus(txId)).thenReturn(Future.exception(new SynchroException))
//        txConsumer.process(1)
//        verify(txRepositoryM, times(0)).finishActivity(txId)
//        verify(txRepositoryM, times(1)).reclaimTX(txId)
//      }
//    }
//  }
//}
