package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import javax.annotation.PostConstruct

import com.twitter.util.Await
import org.apache.commons.lang3.StringUtils
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
            val txId = process(i)
            //取不到值sleep一秒
            if (StringUtils.isBlank(txId)) {
              Thread.sleep(1000)
              logger.info(s"TXConsumer[${i}] sleep ...")
            }
          }
        }
      }, 2, TimeUnit.SECONDS)
    }
  }

  /**
    * 处理事务
    * @param index
    * @return
    */
  def process(index: Int): String = {
    logger.info(s"TXConsumer[${index}] start ...")
    val txId = txManager.consumerTX()
    if (StringUtils.isNotBlank(txId)) {
      try {
        //finish Activity
        Await.result(txManager.finishActivity(txId))
      } catch {
        case t:Throwable => {
          logger.error(s"finishActivity tx:[${txId}] error", t)
        }
      }
    }
    logger.info(s"TXConsumer[${index}] end ...")
    txId
  }
}