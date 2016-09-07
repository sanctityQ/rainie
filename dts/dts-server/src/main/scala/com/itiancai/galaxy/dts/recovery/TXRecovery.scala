package com.itiancai.galaxy.dts.recovery

import javax.annotation.PostConstruct

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TXRecovery {
  @Autowired
  val recoveryTask: RecoveryTask = null
  @Autowired
  val reclaimTask: ReclaimTask = null
  @Autowired
  val alarmTask: AlarmTask = null

  val logger = LoggerFactory.getLogger(getClass)

  @PostConstruct
  def init(): Unit = {
    recoveryTask.execute()
    reclaimTask.execute()
    alarmTask.execute()
  }
}

