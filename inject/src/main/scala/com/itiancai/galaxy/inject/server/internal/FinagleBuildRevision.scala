package com.itiancai.galaxy.inject.server.internal

import com.itiancai.galaxy.inject.{Injector, Logging}
import com.twitter.finagle.TwitterFinaglePrivObjFactory
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.util.NonFatal
import com.twitter.util.U64._

object FinagleBuildRevision extends Logging {

  def register(injector: Injector): Unit = {
    TwitterFinaglePrivObjFactory.finagleInit().finagleBuildRevision match {
      case "?" =>
        warn("Unable to resolve Finagle revision.")
      case revision =>
        info(s"Resolved Finagle build revision: (rev=$revision)")
        injector.instance[StatsReceiver].scope("finagle").provideGauge("build/revision") {
          convertBuildRevision(revision).toFloat
        }
    }
  }

  /* Private */

  private[server] def convertBuildRevision(revision: String): Long = {
    try {
      revision.take(10).toU64Long
    } catch {
      case NonFatal(e) =>
        error(s"Unable to convert Finagle build revision to long: ${e.getMessage}")
        -1L
    }
  }
}
