package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.domain.{IDFactory, Status}
import com.itiancai.galaxy.dts.interceptor.{ExtendTransactionAttribute, TransactionAttribute}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.support.{ActionTransactionManager, ActivityTransactionManager}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}


class ActivityTransactionManagerTest extends WordSpec with BeforeAndAfterEach {

  val dtsRepositoryM = mock(classOf[DTSRepository])

  val actionManagerM = mock(classOf[ActionTransactionManager])

  val idFactoryM = mock(classOf[IDFactory])

  val activityManager = new ActivityTransactionManager(
    dtsRepositoryM, actionManagerM, idFactoryM
  )
    override protected def beforeEach(): Unit = {
    reset(dtsRepositoryM, actionManagerM, idFactoryM)
  }

  "activity-begin" when {
    "activity start success" should {
      "result = true" in {
        trait Attribute extends TransactionAttribute with ExtendTransactionAttribute
        val attribute = new Attribute {
          override def name(): String = "p2p:lending:invest"
          override def paramValue(): String = "00102809-43D0-4FD4-9F8A-8676A5EE02D6"
          override def timeOut_(): Int = 1000
        }
        when(idFactoryM.generateTxId(attribute.name())).thenReturn("tx:p2p:lending:invest:1567e4ba90c19f34097c")

        val txStatus = activityManager.begin(attribute)
        verify(dtsRepositoryM, times(1)).saveActivity(any())
        assert(txStatus.txId() == "tx:p2p:lending:invest:1567e4ba90c19f34097c")
      }
    }
  }

  "activity-commit" when {
    "activity  commit success" should {
      "result = true" in {
        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c",Status.Activity.SUCCESS)).thenReturn(true)
        when(dtsRepositoryM.lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c",Status.Activity.SUCCESS)).thenReturn(true)
        activityManager.commit(txStatus1)
        val f1 = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(1)).commit(org.mockito.Matchers.eq(txStatus1))(f1.capture())
      }
    }
  }

  "activity-commit" when {
    "activity  commit false" should {
      "updateActivityStatus return false" in {

        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.SUCCESS)).thenThrow(new RuntimeException)

        val ex = intercept[RuntimeException] {
          activityManager.commit(txStatus1)
        }
        verify(dtsRepositoryM, times(0)).lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.SUCCESS)

        val finish = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(0)).commit(org.mockito.Matchers.eq(txStatus1))(finish.capture())
        assert(ex.isInstanceOf[RuntimeException])
      }
    }
  }

  "activity-commit" when {
    "activity  commit false" should {
      "lockTX return false" in {

        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.SUCCESS)).thenReturn(true)

        when(dtsRepositoryM.lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.SUCCESS)).thenReturn(false)

        activityManager.commit(txStatus1)
        val finish = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(0)).commit(org.mockito.Matchers.eq(txStatus1))(finish.capture())
      }
    }
  }

  //////////////////////////////////////////////////////////////////////

  "activity-rollback" when {
    "activity  rollback success" should {
      "result = true" in {
        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)).thenReturn(true)

        when(dtsRepositoryM.lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)).thenReturn(true)

        activityManager.rollback(txStatus1)
        val f1 = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(1)).rollback(org.mockito.Matchers.eq(txStatus1))(f1.capture())
      }
    }
  }

  "activity-rollback" when {
    "activity  rollback false" should {
      "rollback updateActivityStatus Exception " in {

        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)).thenThrow(new RuntimeException)

        val ex = intercept[RuntimeException] {
          activityManager.rollback(txStatus1)
        }
        verify(dtsRepositoryM, times(0)).lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)

        val finish = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(0)).rollback(org.mockito.Matchers.eq(txStatus1))(finish.capture())
        assert(ex.isInstanceOf[RuntimeException])
      }
    }
  }

  "activity-rollback" when {
    "activity  rollback false" should {
      "lockTX false" in {
        val txStatus1: TransactionStatus = new TransactionStatus {
          override def txId(): String = "tx:p2p:lending:invest:1567e4ba90c19f34097c"
        }
        when(dtsRepositoryM.updateActivityStatus("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)).thenReturn(true)

        when(dtsRepositoryM.lockTX("tx:p2p:lending:invest:1567e4ba90c19f34097c"
          ,Status.Activity.FAIL)).thenReturn(false)

        activityManager.rollback(txStatus1)
        val finish = ArgumentCaptor.forClass(classOf[Function1[String, Unit]])
        verify(actionManagerM, times(0)).commit(org.mockito.Matchers.eq(txStatus1))(finish.capture())
      }
    }
  }

}
