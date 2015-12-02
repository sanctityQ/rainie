package com.itiancai.galaxy.inject

import com.twitter.app.{App => TwitterUtilApp}

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._


abstract class AbstractApp extends App

trait App extends TwitterUtilApp with Logging with ContextConfig{

  private[inject] var runAppMain: Boolean = true

  private[inject] var appStarted: Boolean = false

  private[inject] lazy val requiredModules = modules ++ javaModules.asScala ++ frameworkModules

  private var installedModules: InstalledModules = _

  init {
    info("App starting")

  }

  def injector: Injector = {
    if (installedModules == null)
      throw new Exception("injector is not available before main() is called")
    else
      installedModules.injector
  }



  def main() {

    installedModules = loadModules()
    installedModules.postStartup()

    postStartup()
    info("Warming up.")
    warmup()
    beforePostWarmup()
    postWarmup()
    afterPostWarmup()

    info("App started.")
    appStarted = true

    onExit {
      installedModules.shutdown()
    }

    callAppMain()
  }


  /**
   * Default modules can be overridden in production by overriding methods in your App or Server
   * We take special care to make sure the module is not null, since a common bug
   * is overriding the default methods using a val instead of a def
   */
  protected def addFrameworkModule(module: Module) {
    assert(
      module != null,
      "Module cannot be null. If you are overriding a default module, " +
        "override it with 'def' instead of 'val'")
    frameworkModules += module
  }

  protected def addFrameworkModules(modules: Module*) {
    modules foreach addFrameworkModule
  }


  /** Method to be called after injector creation */
  protected def postStartup() {
  }

  /** Warmup method to be called before postWarmup */
  protected def warmup() {
  }

  /** Method to be called after successful warmup */
  protected def postWarmup() {
  }

  protected def beforePostWarmup() {
  }

  protected def afterPostWarmup() {
  }


  protected def modules: Seq[Module] = Seq()

  /** Production modules from Java */
  protected def javaModules: java.util.Collection[Module] = new java.util.ArrayList[Module]()

  private val frameworkModules: ArrayBuffer[Module] = ArrayBuffer()


  def appMain() {
  }

  /* Private */

  private def callAppMain() {
    if (runAppMain) {
      try {
        appMain()
      } catch {
        case e: Throwable =>
          error("Error in appMain", e)
          throw e
      }
    }
  }

  protected[inject] def loadModules() = {
    InstalledModules.create(this, requiredModules)
  }

}
