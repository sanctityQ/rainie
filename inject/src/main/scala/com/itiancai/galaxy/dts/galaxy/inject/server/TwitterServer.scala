package com.itiancai.galaxy.dts.galaxy.inject.server

import com.itiancai.galaxy.inject.server.internal.{PromoteToOldGenUtils, FinagleBuildRevision}
import com.itiancai.galaxy.inject.{Logging, Module}
import com.itiancai.galaxy.inject.modules.StatsReceiverModule
import com.itiancai.galaxy.inject.App
import com.twitter.finagle.TwitterFinaglePrivObjFactory
import com.twitter.finagle.http.HttpMuxer
import com.twitter.server.Lifecycle.Warmup
import com.twitter.server.handler.ReplyHandler
import com.twitter.util.Await

/** AbstractTwitterServer for usage from Java */
abstract class AbstractTwitterServer extends TwitterServer

trait TwitterServer
  extends App
  with com.twitter.server.TwitterServer
  with Ports
  with Warmup
  with Logging {

  addFrameworkModule(statsModule)

  /* Protected */

  protected def statsModule: Module = StatsReceiverModule

  /** Resolve all Finagle clients before warmup method called */
  protected def resolveFinagleClientsOnStartup = true

  protected def waitForServer() {
    Await.ready(adminHttpServer)
  }

  /* Overrides */

  override final def main() {
    super.main() // Call InjectApp.main() to create injector

    info("Startup complete, server ready.")
    waitForServer()
  }

  /** Method to be called after injector creation */
  override protected def postStartup() {
    super.postStartup()

    if (resolveFinagleClientsOnStartup) {
      info("Resolving Finagle clients before warmup")
      Await.ready {
        TwitterFinaglePrivObjFactory.clientRegistry.expAllRegisteredClientsResolved() onSuccess { clients =>
          info("Done resolving clients: " + clients.mkString("[", ", ", "]") + ".")
        }
      }
    }

    FinagleBuildRevision.register(injector)
  }

  /**
   * After warmup completes, we want to run PromoteToOldGen without also signaling
   * that we're healthy since we haven't successfully started our servers yet
   */
  override protected def beforePostWarmup() {
    super.beforePostWarmup()
    PromoteToOldGenUtils.beforeServing()
  }

  /**
   * After postWarmup, all external servers have been started, and we can now
   * enable our health endpoint
   */
  override protected def afterPostWarmup() {
    super.afterPostWarmup()
    info("Enabling health endpoint on port " + PortUtils.getPort(adminHttpServer))
    HttpMuxer.addHandler("/health", new ReplyHandler("OK\n"))
  }
}
