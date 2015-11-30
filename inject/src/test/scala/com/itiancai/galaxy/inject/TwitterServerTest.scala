package com.itiancai.galaxy.inject

import com.itiancai.galaxy.inject.ContextConfig
import com.itiancai.galaxy.inject.server.{PortUtils, TwitterServer}
import com.twitter.util.{Future, NonFatal}
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.specs2.runner.JUnitRunner
import org.springframework.context.annotation.Configuration

@RunWith(classOf[JUnitRunner])
class TwitterServerTest extends WordSpec {
  private var startupFailedThrowable: Option[Throwable] = None
  val maxStartupTimeSeconds: Int = 60

  var server: Option[TwitterServer] = None
  var appName = None


  private def waitForAppStarted() {
    for (i <- 1 to maxStartupTimeSeconds) {
      info("Waiting for warmup phases to complete...")

      if (startupFailedThrowable.isDefined) {
        println(s"\nEmbedded app $appName failed to startup")
        throw startupFailedThrowable.get
      }

      if (server.get.appStarted) {
        started = true
        logAppStartup()
        return
      }

      Thread.sleep(1000)
    }
    throw new Exception(s"App: $appName failed to startup within $maxStartupTimeSeconds seconds.")
  }

  "A class" in {
    val server: TwitterServer = new TwitterServer {
      override protected def configureSpring(): ContextConfig = {
        new ContextConfig {

          override def scanPackageName(): Seq[String] = Seq("com.itiancai.inject")

          override def registerClass(): Seq[Class[_]] = Seq(classOf[Configure])
        }
      }
    }
    this.server = Some(server)

    val mainRunnerFuturePool = PoolUtils.newFixedPool("Embedded " + server.name)
     var _mainResult: Future[Unit] = mainRunnerFuturePool {
       try {
         val arr = Array("-admin.port=" + PortUtils.ephemeralLoopback)
         server.nonExitingMain(arr)
       } catch {
         case e: OutOfMemoryError if e.getMessage == "PermGen space" =>
           println("OutOfMemoryError(PermGen) in server startup. " +
             "This is most likely due to the incorrect setting of a client " +
             "flag (not defined or invalid). Increase your permgen to see the exact error message (e.g. -XX:MaxPermSize=256m)")
           e.printStackTrace()
           System.exit(-1)
         case e if !NonFatal.isNonFatal(e) =>
           println("Fatal exception in server startup.")
           throw new Exception(e) // Need to rethrow as a NonFatal for FuturePool to "see" the exception :/
       }
     } onFailure { e =>
       //If we rethrow, the exception will be suppressed by the Future Pool's monitor. Instead we save off the exception and rethrow outside the pool
         startupFailedThrowable = Some(e)
     }
    _mainResult.isDone
//     println("xxxxxxxxxxx"+server.httpExternalPort.getOrElse(throw new Exception("External HTTP port not bound")))
  }
}

@Configuration
class Configure {

}
