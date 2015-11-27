package com.itiancai.galaxy.inject



trait AbstractModule extends Module with ModuleLifeCycle with Logging {

  final override def config() = {
    configure(ContextHolder.binder)
  }

  protected def configure(bind: Binder): Unit = {}

}
