package com.itiancai.galaxy.inject


abstract class AbstractModule extends Module with ModuleLifeCycle with Logging {

  protected var binder: Binder = _

  final override def config(binder: Binder) = {
    this.binder = binder
    configure()
  }

  protected def configure() = {};

}
