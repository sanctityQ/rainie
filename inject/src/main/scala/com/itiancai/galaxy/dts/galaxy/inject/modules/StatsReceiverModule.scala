package com.itiancai.galaxy.dts.galaxy.inject.modules

import com.itiancai.galaxy.inject.{AbstractModule}
import com.twitter.finagle.stats.LoadedStatsReceiver


object StatsReceiverModule extends AbstractModule {
  override protected def configure() {
    binder bind LoadedStatsReceiver
  }
}
