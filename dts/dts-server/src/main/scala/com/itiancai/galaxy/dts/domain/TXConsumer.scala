package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, TimeUnit}
import javax.annotation.PostConstruct

import com.twitter.util.Await
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

@Component
class TXConsumer {

  @Autowired
  val txManager: TXManager = null

  /* 补偿queue key*/
  @Value("${tx.compensate.consumer.poolSize}")
  val consumerPoolSize :Int = 0

  val logger = LoggerFactory.getLogger(getClass)

  @PostConstruct
  def init(): Unit = {
    //消费tx-pool
    val scheduledService = Executors.newScheduledThreadPool(consumerPoolSize)
    for(index <- 0 until 8) {
      scheduledService.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = {
          val txList = txManager.listUnfinished(index)
          txList.foreach(txId => {
            //处理tx
            logger.info(s"Consumer ${index} handle tx[${txId}] begin")
            try {
              Await.result(txManager.finishActivity(txId))
            } catch {
              case t:Throwable => {
                logger.error(s"Consumer ${index} handle tx[${txId}]  error", t)
              }
            }
            logger.info(s"Consumer ${index} handle tx[${txId}] end")
          })
        }
      }, 1000, 500, TimeUnit.MILLISECONDS)
    }
  }
}