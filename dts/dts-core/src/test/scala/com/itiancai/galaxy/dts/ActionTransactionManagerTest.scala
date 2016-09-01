package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.domain.{Action, IDFactory, Status}
import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.recovery._
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.support.ActionTransactionManager
import com.twitter.util.Future
import org.mockito.ArgumentMatcher
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}


class ActionTransactionManagerTest  extends WordSpec with BeforeAndAfterEach{

  val dtsRepositoryM = mock(classOf[DTSRepository])

  val idFactoryM = mock(classOf[IDFactory])

  val recoveryClientM = mock(classOf[RecoveryClient])

  val actionManager = new ActionTransactionManager(
    dtsRepositoryM, new RecoveryClientSource {
      override def findRecoveryClient(recoverServiceName: RecoverServiceName): RecoveryClient = {
        recoveryClientM
      }
      override def getTransactionClient(recoverServiceName: RecoverServiceName): RecoveryClient = {
        findRecoveryClient(recoverServiceName)
      }
    }, idFactoryM
  )

  override protected def beforeEach(): Unit = {
    reset(dtsRepositoryM, idFactoryM, recoveryClientM)
  }

  "action-begin" when {
    "action start success" should {
      "result = true" in {
        val attribute = new TransactionAttribute {
          override def name(): String = "p2p:lending:invest"
          override def paramValue(): String = "00102809-43D0-4FD4-9F8A-8676A5EE02D6"
        }
        when(idFactoryM.getActionId(attribute.name())).thenReturn("ac:p2p:lending:invest:1567e3e136d124716ba5")
        TXIdLocal.let_txId("tx:p2p:lending:invest:1567e4ba90c19f34097c"){
          val txStatus = actionManager.begin(attribute)
          verify(dtsRepositoryM, times(1)).saveAction(any())
          assert(txStatus.txId() == "ac:p2p:lending:invest:1567e3e136d124716ba5")
          TXIdLocal.clear_txId
        }
      }
    }
  }

  "action-prepare" when {
    "action prepare success" should {
      "result = true" in {
        actionManager.prepare("ac:p2p:lending:invest:1567e3e136d124716ba5")
        verify(dtsRepositoryM, times(1)).prepareAction("ac:p2p:lending:invest:1567e3e136d124716ba5")
      }
    }
  }


  "action-commit" when {
    "action commit success" should {
      "result = true" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")
        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))
        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="commit" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenReturn(Future(true))
        actionManager.commit(txStatus)(dtsRepositoryM.finishActivity)
        verify(dtsRepositoryM, times(1)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.SUCCESS)
        verify(dtsRepositoryM, times(1)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
      }
    }
  }

  "action-commit" when {
    "action commit fail" should {
      "recoveryClientM return = false" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")

        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))

        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="commit" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenReturn(Future(false))

        actionManager.commit(txStatus)(dtsRepositoryM.finishActivity)

        verify(dtsRepositoryM, times(0)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.SUCCESS)
        verify(dtsRepositoryM, times(0)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
      }
    }
  }

  "action-commit" when {
    "action commit fail" should {
      "recoveryClientM return = Exception" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")

        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))

        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="commit" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenThrow(new RuntimeException)

        val ex = intercept[RuntimeException] {
          actionManager.commit(txStatus)(dtsRepositoryM.finishActivity)
        }
        verify(dtsRepositoryM, times(0)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.SUCCESS)
        verify(dtsRepositoryM, times(0)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
        assert(ex.isInstanceOf[RuntimeException])
      }
    }
  }

  //////////////////////////////////////////////////////////////

  "action-rollback" when {
    "action rollback success" should {
      "result = true" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")

        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))

        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="rollback" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenReturn(Future(true))

        actionManager.rollback(txStatus)(dtsRepositoryM.finishActivity)
        verify(dtsRepositoryM, times(1)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.FAIL)
        verify(dtsRepositoryM, times(1)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
      }
    }
  }

  "action-rollback" when {
    "action rollback fail" should {
      "recoveryClientM return = false" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")

        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))

        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="rollback" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenReturn(Future(false))

        actionManager.rollback(txStatus)(dtsRepositoryM.finishActivity)

        verify(dtsRepositoryM, times(0)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.FAIL)
        verify(dtsRepositoryM, times(0)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
      }
    }
  }

  "action-rollback" when {
    "action rollback fail" should {
      "recoveryClientM return = Exception" in {
        val txStatus = new TransactionStatus() {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        val action = new Action("tx:p2p:lending:invest:1567e4ba90c19f34097c",
          "ac:p2p:lending:invest:1567e3e136d124716ba5",
          Status.Action.PREPARE,"p2p:lending:invest","00102809-43D0-4FD4-9F8A-8676A5EE02D6")

        when(dtsRepositoryM.listActionByTxId("tx:p2p:lending:invest:1567e4ba90c19f34097c")).thenReturn(List(action))

        when(recoveryClientM.request(argThat(new ArgumentMatcher[ActionRequest] {
          override def matches(argument: scala.Any): Boolean = {
            val actionRequest = argument.asInstanceOf[ActionRequest]
            val name = actionRequest.name
            val methodName = actionRequest.actionMethod
            val instructionId = actionRequest.instructionId
            name =="invest" && methodName =="rollback" && instructionId =="00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          }
        })
        )).thenThrow(new RuntimeException)

        val ex = intercept[RuntimeException] {
          actionManager.rollback(txStatus)(dtsRepositoryM.finishActivity)
        }
        verify(dtsRepositoryM, times(0)).finishAction("ac:p2p:lending:invest:1567e3e136d124716ba5",Status.Action.FAIL)
        verify(dtsRepositoryM, times(0)).finishActivity("tx:p2p:lending:invest:1567e4ba90c19f34097c")
        assert(ex.isInstanceOf[RuntimeException])
      }
    }
  }


}
