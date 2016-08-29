package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, TimeUnit}
import javax.annotation.PostConstruct

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * 回收处理超时的事务
  * Created by bao on 16/8/2.
  */
@Component
class TXProducer {
  @Autowired
  val tXManager: TXManager = null

  val logger = LoggerFactory.getLogger(getClass)
  val scheduledService = Executors.newSingleThreadScheduledExecutor

  /**
    * 回收处理超时的事务
    */
  @PostConstruct
  def init(): Unit = {
    //TODO 时间待修改
    scheduledService.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        logger.info("TXProducer execute start ...")
        //回收处理超时任务
        tXManager.reclaimHandleTimeoutTX()
        logger.info("TXProducer execute end ...")
      }
    }, 1, 29, TimeUnit.SECONDS)
  }
}


