package com.itiancai.galaxy.dts.domain

import com.twitter.util.Future
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

/**
  * Created by bao on 16/8/11.
  */
class TXConsumerTest extends WordSpec with BeforeAndAfterEach {

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
}
