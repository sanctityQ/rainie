package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.utils._
import com.twitter.util.Await
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

/**
  * 事务补偿消费者
  * Created by bao on 16/8/2.
  */
@Component
class TXConsumer {

  val logger = LoggerFactory.getLogger(getClass)

  /* 补偿queue key*/
  @Value("${tx.compensate.consumer.poolSize}")
  val consumerPoolSize :Int = 0
  /* 补偿queue key*/
  @Value("${tx.compensate.queue}")
  val compensateQueue: String = null

  @Autowired
  val redisService: RedisService = null

  @Autowired
  val txManager: TXManager = null

  var executorService: ScheduledExecutorService = null

  //TODO sleep时间  启动延迟时间  间隔 配置文件
  //TODO 配置文件可控制是否启动任务
  @PostConstruct
  def init(): Unit = {
    executorService = Executors.newScheduledThreadPool(consumerPoolSize)
    for (i <- 0 until consumerPoolSize) {
      executorService.schedule(new Runnable {
        override def run(): Unit = {
          while (true) {
            process(i)
          }
        }
      }, 2, TimeUnit.SECONDS)
    }
  }

  def process(index: Int): Unit = {
    logger.info(s"TXConsumer[${index}] start ...")
    val txId = {
      try {
        redisService.rpop(compensateQueue)
      } catch {
        case t: Throwable => {
          logger.error(s"redisService rpop ${compensateQueue} error")
          null
        }
      }
    }
    if (txId == null) {
      //取不到值sleep一秒
      Thread.sleep(1000)
      logger.info(s"TXConsumer[${index}] sleep ...")
    } else {
      //finish Activity
      val result_f = txManager.finishActivity(txId)
      try {
        Await.result(result_f)
      } catch {
        case t:Throwable => {
          logger.error(s"finishActivity tx:[${txId}] error", t)
        }
      }
    }
  }
}