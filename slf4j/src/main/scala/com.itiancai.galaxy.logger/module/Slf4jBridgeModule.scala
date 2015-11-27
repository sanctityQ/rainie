package com.itiancai.galaxy.logger.module

import com.itiancai.galaxy.inject.{AbstractModule, Injector}
import org.slf4j.bridge.SLF4JBridgeHandler

object Slf4jBridgeModule extends AbstractModule {
  override def singletonStartup(injector: Injector) {
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
  }

}
