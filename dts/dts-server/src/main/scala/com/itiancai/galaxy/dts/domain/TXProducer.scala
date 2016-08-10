package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, TimeUnit}
import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.dao.ActivityDao
import com.itiancai.galaxy.dts.repository.TXRepository
import com.itiancai.galaxy.dts.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * 收集未完成的事务 & 回收处理超时的事务
  * Created by bao on 16/8/2.
  */
@Component
class TXProducer {

  val logger = LoggerFactory.getLogger(getClass)

  /*
    收集数据

    collect  0 未收集

    finish = 0

    1: status=UNKNOWN & now > c_time + time_out
    2: status=SUCCESS | status=FAIL

    放入 redis 队列
  */
  val executorService = Executors.newSingleThreadScheduledExecutor

  @Autowired
  val redisService:RedisService = null

  @Autowired
  val txRepository:TXRepository = null

  @Autowired
  val activityDao:ActivityDao = null

  /**
    * //TODO 配置文件可控制是否启动任务
    * 收集未完成的事务 & 回收处理超时的事务
    */
  @PostConstruct
  def init(): Unit = {
    executorService.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        logger.info("TXProducer execute start ...")
        //查询所有未完成的数据
        val list = txRepository.listUnfinished()
        list.foreach(txId => {
          try {
            txRepository.collectTX(txId)
          } catch {
            case t:Throwable => logger.error(s"TXProducer collectTX fail", t)
          }
        })
        //回收处理超时任务
        txRepository.reclaimHandleTimeoutTX();
        logger.info("TXProducer execute end ...")
      }
    }, 1000, 200, TimeUnit.MILLISECONDS)
  }
}


