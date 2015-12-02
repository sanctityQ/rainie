package com.twitter

import java.net.{InetAddress, InetSocketAddress}

import scala.collection.mutable;


class TwitterServerTest extends com.itiancai.galaxy.inject.Test  {
  "TwitterServer.main(args) executes without error" in {
    val twitterServer = new TestTwitterServer
    twitterServer.main(args = Array.empty[String])
    assert(twitterServer.bootstrapSeq ==
      Seq('Init, 'PreMain, 'Main, 'PostMain, 'Exit))
  }
}

class TestTwitterServer extends com.twitter.server.TwitterServer {
//  override val adminPort = flag("admin.port",
//    new InetSocketAddress(InetAddress.getLoopbackAddress, 0), "")

  val bootstrapSeq = mutable.MutableList.empty[Symbol]

  def main() {
    bootstrapSeq += 'Main
  }

  init {
    bootstrapSeq += 'Init
  }

  premain {
    bootstrapSeq += 'PreMain
  }

  onExit {
    bootstrapSeq += 'Exit
  }

  postmain {
    bootstrapSeq += 'PostMain
  }
}





