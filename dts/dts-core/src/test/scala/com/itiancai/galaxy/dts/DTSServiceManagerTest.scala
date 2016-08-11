package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.domain.{Action, IdGenerator, Status}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.utils.RecoveryClientFactory
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.mockito.ArgumentMatcher
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

import scala.collection.JavaConversions._

/**
  * Created by bao on 16/8/11.
  */
class DTSServiceManagerTest extends WordSpec with BeforeAndAfterEach {

  val actionDaoM = mock(classOf[ActionDao])

  val activityDaoM = mock(classOf[ActivityDao])

  val idGeneratorM = mock(classOf[IdGenerator])

  val dtsRepositoryM = mock(classOf[DTSRepository])

  val clientM: Service[Request, Response] = mock(classOf[Service[Request, Response]])

  val dtsManager = new DTSServiceManager {
    override val clientFactory: RecoveryClientFactory = new RecoveryClientFactory {
      override def getClient(sysName: String, moduleName: String): Future[Service[Request, Response]] = {
        Future(clientM)
      }
    }
    override val actionDao = actionDaoM
    override val activityDao = activityDaoM
    override val idGenerator = idGeneratorM
    override val dtsRepository = dtsRepositoryM
  }

  override protected def beforeEach(): Unit = {
    reset(actionDaoM, clientM, activityDaoM, idGeneratorM, dtsRepositoryM)
  }

  "DTSManager-finishActions" when {

    "TX status = SUCCESS & all action success" should {
      "result = true" in {
        val txId = "txId1"
        val action1 = new Action()
        action1.setId(1L)
        action1.setInstructionId("InstructionId1")
        action1.setActionId("action1")
        action1.setServiceName("p2p:interact:trigger")
        action1.setTxId(txId)
        action1.setStatus(Status.Action.PREPARE)
        val action2 = new Action()
        action2.setId(2L)
        action2.setInstructionId("InstructionId2")
        action2.setActionId("action2")
        action2.setServiceName("p2p:user:bindInviteCode")
        action2.setTxId(txId)
        action2.setStatus(Status.Action.PREPARE)
        when(actionDaoM.findByTxId(txId)).thenReturn(List(action1, action2))
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val instructionId = requet.getParam("id")
            val serviceName = requet.getParam("name")
            val method = requet.getParam("method")
            List("InstructionId1", "InstructionId2").contains(instructionId) &&
              List("trigger", "bindInviteCode").contains(serviceName) &&
              method == "commit"
          }
        }))).thenReturn({
          val response = Response()
          response.setContentString("true")
          Future(response)
        })
        when(dtsRepositoryM.finishAction(argThat(new ArgumentMatcher[String] {
          override def matches(argument: scala.Any): Boolean = {
            val actionId = argument.asInstanceOf[String]
            List("action1", "action2").contains(actionId)
          }
        }), org.mockito.Matchers.eq(Status.Action.SUCCESS))).thenReturn(true)
        val result_f = dtsManager.finishActions(txId, Status.Activity.SUCCESS)
        assert(Await.result(result_f))
      }
    }

    "TX status = SUCCESS & finish action fail" should {
      "result = false" in {
        val txId = "txId1"
        val action1 = new Action()
        action1.setId(1L)
        action1.setInstructionId("InstructionId1")
        action1.setActionId("action1")
        action1.setServiceName("p2p:interact:trigger")
        action1.setTxId(txId)
        action1.setStatus(Status.Action.PREPARE)
        val action2 = new Action()
        action2.setId(2L)
        action2.setInstructionId("InstructionId2")
        action2.setActionId("action2")
        action2.setServiceName("p2p:user:bindInviteCode")
        action2.setTxId(txId)
        action2.setStatus(Status.Action.PREPARE)
        when(actionDaoM.findByTxId(txId)).thenReturn(List(action1, action2))
        when(clientM.apply(argThat(new ArgumentMatcher[Request] {
          override def matches(argument: scala.Any): Boolean = {
            val requet = argument.asInstanceOf[Request]
            val instructionId = requet.getParam("id")
            val serviceName = requet.getParam("name")
            val method = requet.getParam("method")
            List("InstructionId1", "InstructionId2").contains(instructionId) &&
              List("trigger", "bindInviteCode").contains(serviceName) &&
              method == "commit"
          }
        }))).thenReturn({
          val response = Response()
          response.setContentString("false")
          Future(response)
        })
        val result_f = dtsManager.finishActions(txId, Status.Activity.SUCCESS)
        assert(!Await.result(result_f))
        verify(dtsRepositoryM, never()).finishAction(argThat(new ArgumentMatcher[String] {
          override def matches(argument: scala.Any): Boolean = {
            val actionId = argument.asInstanceOf[String]
            List("action1", "action2").contains(actionId)
          }
        }), org.mockito.Matchers.eq(Status.Action.SUCCESS))
      }
    }
  }
}
