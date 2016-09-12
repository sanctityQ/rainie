package com.itiancai.galaxy.dts.domain

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status => HttpStatus}
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

class TXRecoveryTest extends WordSpec with BeforeAndAfterEach {

  val clientM: Service[Request, Response] = mock(classOf[Service[Request, Response]])

  override protected def beforeEach(): Unit = {
    reset()
  }

  "TXManager-synchroActivityStatus" when {
    "status=UNKNOWN & search activity response = 0" should {
      "status = SUCCESS" in {

      }
    }

  }
}
