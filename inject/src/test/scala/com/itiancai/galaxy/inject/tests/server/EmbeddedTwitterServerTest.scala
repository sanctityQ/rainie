package com.itiancai.galaxy.inject.tests.server

import com.itiancai.galaxy.inject.{Configure, ContextConfig, Test}
import com.itiancai.galaxy.inject.server.TwitterServer
import com.twitter.finagle.http.Status


class EmbeddedTwitterServerTest extends Test {
  "server" should {
    "start" in {
      val twitterServer = new TwitterServer {
        addAnnotationClass[Configure]
       // override protected def configureSpring(): ContextConfig =  TestContextConfig
      }
      val embeddedServer = new EmbeddedTwitterServer(twitterServer)

      embeddedServer.httpGetAdmin(
        "/health",
        andExpect = Status.Ok,
        withBody = "OK\n")

      embeddedServer.close()
    }

//    "fail if server is a singleton" in {
//      intercept[IllegalArgumentException] {
//        new EmbeddedTwitterServer(SingletonServer)
//      }
//    }
  }
}
