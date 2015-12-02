package com.itiancai.galaxy.inject

import java.net.{InetSocketAddress, InetAddress}

import com.itiancai.galaxy.inject.server.{PortUtils, TwitterServer}
import com.twitter.util.{Future, NonFatal}
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.specs2.runner.JUnitRunner
import org.springframework.context.annotation.{ComponentScan, Configuration}

import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class TwitterServerTest extends WordSpec {
  private var startupFailedThrowable: Option[Throwable] = None
  val maxStartupTimeSeconds: Int = 60

  var server: TwitterServer = new TwitterServer {
    addAnnotationClass[Configure]
//    override protected def configureSpring(): ContextConfig = {
//      new ContextConfig {
//
//        //override def scanPackageName(): Seq[String] = Seq("com.itiancai.galaxy.inject.tests")
//
//       // override def registerClass(): Seq[Class[_]] = Seq(classOf[Configure])
//        addAnnotationClass[Configure]
//      }
//    }
  }

  var appName = server.name

  private var started = false
  private def waitForAppStarted() {
    for (i <- 1 to maxStartupTimeSeconds) {
      info("Waiting for warmup phases to complete...")

      if (startupFailedThrowable.isDefined) {
        println(s"\nEmbedded app $appName failed to startup")
        throw startupFailedThrowable.get
      }

      if (server.appStarted) {
        started = true
        logAppStartup()
        return
      }

      Thread.sleep(1000)
    }
    throw new Exception(s"App: $appName failed to startup within $maxStartupTimeSeconds seconds.")
  }

  def infoBanner(str: String) {
    info("\n")
    info("=" * 75)
    info(str)
    info("=" * 75)
  }

  protected def logAppStartup() = {
    infoBanner("App warmup completed: " + appName)
  }

  "A class" in {

    val mainRunnerFuturePool = PoolUtils.newFixedPool("Embedded " + appName)
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
    waitForAppStarted()
    started = true
     println("xxxxxxxxxxx"+server.httpExternalPort.getOrElse(throw new Exception("External HTTP port not bound")))
  }


  "Twitter server test " in {
    val twitterServer = new TestTwitterServer
    assert(twitterServer.bootstrapSeq.isEmpty)
  }

  "TwitterServer.main(args) executes without error" in {
    val twitterServer = new TestTwitterServer
    twitterServer.main(args = Array.empty[String])
    assert(twitterServer.bootstrapSeq ==
      Seq('Init, 'PreMain, 'Main, 'PostMain, 'Exit))
  }
}

@Configuration
@ComponentScan(Array("com.itiancai.galaxy.inject.tests"))
class Configure{

}
class TestTwitterServer extends com.twitter.server.TwitterServer {
  override val adminPort = flag("admin.port",
    new InetSocketAddress(InetAddress.getLoopbackAddress, 0), "")

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