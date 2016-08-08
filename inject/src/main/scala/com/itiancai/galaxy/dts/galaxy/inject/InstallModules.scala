package com.itiancai.galaxy.dts.galaxy.inject

object InstalledModules {

  def create(contextConfig: ContextConfig, modules: Seq[Module]): InstalledModules = {
    new InstalledModules(ContextHolder.injector(contextConfig, modules), modules)
  }
}


case class InstalledModules(injector: Injector, modules: Seq[Module]) extends Logging {

  def postStartup() {
    modules foreach {
      case injectModule: ModuleLifeCycle =>
        try {
          injectModule.singletonStartup(injector)
        } catch {
          case e: Throwable =>
            error("Startup method error in " + injectModule, e)
            throw e
        }
      case _ =>
    }
  }

  // Note: We don't rethrow so that all modules have a change to shutdown
  def shutdown() {
    modules foreach {
      case injectModule: ModuleLifeCycle =>
        try {
          injectModule.singletonShutdown(injector)
        } catch {
          case e: Throwable =>
            error("Shutdown method error in " + injectModule, e)
        }
      case _ =>
    }
  }
}