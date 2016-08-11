package com.itiancai.galaxy.dts.domain

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import com.itiancai.galaxy.dts.repository.DTSRepository
import com.itiancai.galaxy.dts.utils.{NameResolver, RecoveryClientFactory, SynchroException}
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * Created by bao on 16/8/11.
  */
@Component
class TXManager {

  val logger = LoggerFactory.getLogger(getClass)
  @Autowired
  val clientFactory: RecoveryClientFactory = null
  @Autowired
  private val actionDao: ActionDao = null
  @Autowired
  private val activityDao: ActivityDao = null
  @Autowired
  val dtsRepository: DTSRepository = null

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
      //get client
      val client = clientFactory.getClient(sysName, moduleName)
      val path = s"${NameResolver.ACTIVITY_HANDLE_PATH}?businessId=${activity.getBusinessId}&businessType=${serviceName}"
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
        if (dtsRepository.updateActivityStatus(activity.getTxId, status)) {
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
}
