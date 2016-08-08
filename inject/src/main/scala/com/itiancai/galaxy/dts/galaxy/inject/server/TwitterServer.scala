package com.itiancai.galaxy.dts.galaxy.inject.server

import java.util.concurrent.ConcurrentLinkedQueue

import com.itiancai.galaxy.inject.annotations.Lifecycle
import com.itiancai.galaxy.inject.server.internal.{PromoteToOldGenUtils, FinagleBuildRevision}
import com.itiancai.galaxy.inject.{Logging, Module}
import com.itiancai.galaxy.inject.modules.StatsReceiverModule
import com.itiancai.galaxy.inject.App
import com.twitter.finagle.TwitterFinaglePrivObjFactory
import com.twitter.finagle.http.HttpMuxer
import com.twitter.server.Lifecycle.Warmup
import com.twitter.server.handler.ReplyHandler
import com.twitter.util.{Awaitable, Await}
import scala.collection.JavaConverters._

/** AbstractTwitterServer for usage from Java */
abstract class AbstractTwitterServer extends TwitterServer

trait TwitterServer
  extends App
  with com.twitter.server.TwitterServer
  with Ports
  with Warmup
  with Logging {

  addFrameworkModule(statsModule)

  private val adminAnnounceFlag = flag[String]("admin.announce", "Address for announcing admin server")

  /* Mutable State */

  private[inject] val awaitables: ConcurrentLinkedQueue[Awaitable[_]] = new ConcurrentLinkedQueue()

  premain {
    awaitables.add(adminHttpServer)
  }

  /* Protected */

  override protected def failfastOnFlagsNotParsed: Boolean = true

  /**
    * Name used for registration in the [[com.twitter.util.registry.Library]]
    *
    * @return library name to register in the Library registry.
    */
 // override protected val libraryName: String = "rainie"


  /**
    * If true, the Twitter-Server admin server will be disabled.
    * Note: Disabling the admin server allows services to be deployed into environments where only a single port is allowed
    */
  protected def disableAdminHttpServer: Boolean = false


  /**
    * Default [[com.itiancai.galaxy.inject.AbstractModule]] for providing a [[com.twitter.finagle.stats.StatsReceiver]].
    *
    * @return a [[com.itiancai.galaxy.inject.AbstractModule]] which provides a [[com.twitter.finagle.stats.StatsReceiver]] implementation.
    */
  protected def statsModule: Module = StatsReceiverModule

  /** Resolve all Finagle clients before warmup method called */
  protected def resolveFinagleClientsOnStartup = true


  protected def await[T <: Awaitable[_]](awaitable: T): Unit = {
    assert(awaitable != null, "Cannot call #await() on null Awaitable.")
    this.awaitables.add(awaitable)
  }

  protected def await(awaitables: Awaitable[_]*): Unit = {
    awaitables foreach await
  }

  /* Overrides */

  override final def main() {
    super.main() // Call InjectApp.main() to create injector

    info("Startup complete, server ready.")

    Await.all(awaitables.asScala.toSeq: _*)

//    waitForServer()
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
    * If you override this method to create and bind any external interface or to
    * instantiate any awaitable it is expected that you add the Awaitable (or
    * [[com.twitter.finagle.ListeningServer]]) to the list of Awaitables using the
    * [[await[T <: Awaitable[_]](awaitable: T): Unit]] function.
    *
    * It is NOT expected that you block in this method as you will prevent completion
    * of the server lifecycle.
    */
  @Lifecycle
  override protected def postWarmup(): Unit = {
    super.postWarmup()

    if (disableAdminHttpServer) {
      info("Disabling the Admin HTTP Server since disableAdminHttpServer=true")
      adminHttpServer.close()
    } else {
      for (addr <- adminAnnounceFlag.get) adminHttpServer.announce(addr)
    }
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
