package com.itiancai.galaxy.dts.domain

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.repository.TXRepository
import com.itiancai.galaxy.dts.utils.{ClientFactory, NameResolver, RedisService, SynchroException}
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.util.{Await, Future}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component

import scala.collection.JavaConversions._

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
  val txRepository: TXRepository = null
  @Autowired
  val consumerManager:ConsumerManager = null

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
      //获取主事务状态
      val status_f = consumerManager.synchroActivityStatus(txId)
      //finishActions
      val finishActions_f = status_f.flatMap(consumerManager.finishActions(txId, _))
      val result_f = finishActions_f.map(flag => {
        //如果子事务都处理成功
        if (flag) {
          //修改Activity完成标志
          txRepository.finishActivity(txId)
          logger.info(s"tx:${txId} finish success")
        } else {
          logger.warn(s"tx:${txId} finish fail")
          throw new RuntimeException("finish actions error")
        }
      })
      try {
        Await.result(result_f)
      } catch {
        case t:Throwable => {
          logger.error(s"reclaimTX tx:[${txId}]", t)
          txRepository.reclaimTX(txId)
        }
      }
    }
  }
}

@Component
class ConsumerManager {

  val logger = LoggerFactory.getLogger(getClass)

  @Autowired
  val activityDao: ActivityDao = null
  @Autowired
  val actionDao: ActionDao = null
  @Autowired
  val txRepository: TXRepository = null
  @Autowired
  val clientFactory: ClientFactory = null

  /**
    * 获取主事务状态
    *
    * @return
    */
  def synchroActivityStatus(txId: String): Future[Status.Activity] = {
    //获取txId
    val activity = activityDao.findByTxId(txId)
    if (activity.getStatus != Status.Activity.UNKNOWN) {
      Future(activity.getStatus)
    } else {
      //resolve name
      val (sysName, moduleName, serviceName) = NameResolver.eval(activity.getBusinessType)
      val pathKey = NameResolver.pathKey(sysName, moduleName)
      //get client
      val client = clientFactory.getHttpClient(pathKey)
      val path = s"${NameResolver.ACTIVITY_HANDLE_PATH}?businessId=${activity.getBusinessId}&serviceName=${serviceName}"
      val request = Request(Version.Http11, Method.Get, path)
      client(request).map(response => {
        val status = {
          try {
            logger.info(s"synchroActivityStatus tx:[${activity.getTxId}] response:[${response.contentString}]")
            response.contentString.toInt match {
              //成功
              case 0 => Status.Activity.SUCCESS
              //其他失败
              case _ => Status.Activity.FAIL
            }
          } catch {
            //返回结果
            case t: Throwable => {
              logger.warn(s"synchroActivityStatus tx:[${activity.getTxId}] error", t)
              throw new SynchroException
            }
          }
        }
        //同步主事务状态
        if (txRepository.synchroActivityStatus(activity.getTxId, status)) {
          status //同步成功
        } else {
          //状态已变更,更新失败
          throw new SynchroException
        }
      }).handle({
        case t: Throwable => {
          logger.error(s"synchroActivityStatus tx:[${activity.getTxId}] error", t)
          throw new SynchroException
        }
      })

    }
  }

  /**
    * 完成子事务(提交或回滚)
    *
    * @param txId
    * @param status
    * @return
    */
  def finishActions(txId: String, status: Status.Activity): Future[Boolean] = {
    def finishAction(action: Action): Future[Boolean] = {
      //resolve name
      val (sysName, moduleName, serviceName) = NameResolver.eval(action.getServiceName)
      val pathKey = NameResolver.pathKey(sysName, moduleName)
      //get client
      logger.info(s"sysName:${sysName}, serviceName:${serviceName}")
      val client = clientFactory.getHttpClient(pathKey)
      val method = if (status == Status.Activity.SUCCESS) "commit" else "rollback" //提交 | 回滚
      val path = s"${NameResolver.ACTION_HANDLE_PATH}?instructionId=${action.getInstructionId}&serviceName=${serviceName}&method=${method}"
      val request = Request(Version.Http11, Method.Get, path)
      val actionStatus = if (status == Status.Activity.SUCCESS) Status.Action.SUCCESS else Status.Action.FAIL
      logger.info(s"finishAction ${sysName}-${serviceName}")
      client(request).map(response => {
        val result = response.contentString == "true"
        if(!result) {
          logger.info(s"finishAction:[${action.getId}] ${sysName}-${serviceName} fail, response:${response.contentString}")
          false
        } else {
          logger.info(s"finishAction ${sysName}-${serviceName} success")
          val flag = txRepository.finishAction(action.getActionId, actionStatus)
          if (flag) {
            logger.info(s"action [${action.getId}] updateStatus ${actionStatus} success")
          } else {
            logger.warn(s"action [${action.getId}] status had changed")
          }
          flag //子事务处理成功
        }
      }).handle({
        case t: Throwable => {
          logger.error(s"action [${action.getId}] execute ${method} fail.", t)
          false //子事务失败
        }
      })
    }
    val finishList_f = Future collect actionDao.findByTxId(txId).map(finishAction)
    finishList_f.map(!_.contains(false))
  }
}
