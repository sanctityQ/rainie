package com.itiancai.galaxy.dts.domain

import com.twitter.util.Future
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

/**
  * Created by bao on 16/8/11.
  */
class TXConsumerTest extends WordSpec with BeforeAndAfterEach {

  val txManagerM = mock(classOf[TXManager])

  val txConsumer = new TXConsumer {
    override val txManager = txManagerM
  }

  "txConsumer-process" when {
    "consumerTX = tx1 & finish finishActivity success" should {
      "return tx1" in {
        val txId = "tx1"
        when(txManagerM.consumerTX()).thenReturn(txId)
        when(txManagerM.finishActivity(txId)).thenReturn(Future.Unit)
        assert(txConsumer.process(1) == txId)
        verify(txManagerM, times(1)).consumerTX()
        verify(txManagerM, times(1)).finishActivity(txId)
      }
    }
  }
}
