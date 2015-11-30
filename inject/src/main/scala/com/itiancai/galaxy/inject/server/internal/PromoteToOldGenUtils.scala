package com.itiancai.galaxy.inject.server.internal

import com.twitter.server.Lifecycle.PromoteToOldGen
import com.twitter.server.PrvObjFactory

/**
 * This util is needed to gain access to the private PromoteToOldGen.
 * We can't access this functionality through warmupComplete since our desired startup order is:
 * - Run app specific warmup
 * - PromoteToOldGen
 * - Start external HTTP or Thrift server
 * - Enable /health endpoint
 *
 * TODO: Remove need for this util
 */
object PromoteToOldGenUtils {

  private val promoteToOldGen = PrvObjFactory.promoteToOldGen

  def beforeServing() = {
    promoteToOldGen.beforeServing()
  }
}
