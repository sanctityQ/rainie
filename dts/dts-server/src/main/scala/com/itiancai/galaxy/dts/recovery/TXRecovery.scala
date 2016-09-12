package com.itiancai.galaxy.dts.recovery

import javax.annotation.{PostConstruct, Resource}

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TXRecovery {

  @Resource(name = "recoveryTask")
  val recoveryTask: Task = null
  @Resource(name = "reclaimTask")
  val reclaimTask: Task = null
  @Resource(name = "alarmTask")
  val alarmTask: Task = null

  val logger = LoggerFactory.getLogger(getClass)

  @PostConstruct
  def init(): Unit = {

    recoveryTask.execute()
    logger.info("TXRecovery recoveryTask start ...")

    reclaimTask.execute()
    logger.info("TXRecovery reclaimTask start ...")

    alarmTask.execute()
    logger.info("TXRecovery alarmTask start ...")
  }
}

