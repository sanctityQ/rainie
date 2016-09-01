package com.itiancai.galaxy.dts.interceptor

import javax.annotation.Resource
import javax.inject.Inject

import com.itiancai.galaxy.dts.annotation.{ActionAnnotationAttribute, ActivityAnnotationAttribute}
import com.itiancai.galaxy.dts.support.ActionTransactionManager
import com.itiancai.galaxy.dts.{TXIdLocal, TransactionManager}
import com.twitter.finagle.context.Contexts
import com.twitter.util.Future
import org.aopalliance.intercept.{MethodInterceptor, MethodInvocation}
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.{Bean, Role}
import org.springframework.stereotype.Component


@Component("dtsTransactionInterceptor")
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class TransactionInterceptor @Inject()
(
  transactionAttributeSource: TransactionAttributeSource
) extends MethodInterceptor with Serializable {
  val logger = LoggerFactory.getLogger(getClass)

  @Resource(name = "activityTM")
  val activityTM: TransactionManager = null
  @Resource(name = "actionTM")
  val actionTM: ActionTransactionManager = null

  val txId_key = new Contexts.local.Key[String]



  override def invoke(invocation: MethodInvocation): AnyRef = {

    val targetClass: Class[_] =  if (invocation.getThis != null) AopUtils.getTargetClass(invocation.getThis) else null
    val txAttr: TransactionAttribute = transactionAttributeSource.getTransactionAttribute(invocation.getMethod, targetClass)

    txAttr match {
      case activity: ActivityAnnotationAttribute => {
        activity.parseParamValue(invocation.getArguments)
        //transaction begin
        val transactionStatus = activityTM.begin(activity)
        logger.info(s"tx:${transactionStatus.txId()} begin ...")
        //activityBegin
        TXIdLocal.let_txId(transactionStatus.txId())(
          try {
            val result = invocation.proceed()
            if(result.isInstanceOf[Future[_]]) {
              result.asInstanceOf[Future[_]]
                .onSuccess(r => {
                  activityTM.commit(transactionStatus)
                  logger.info(s"tx:${transactionStatus.txId()} commit!")
                }).onFailure(r => {
                  activityTM.rollback(transactionStatus)
                  logger.info(s"tx:${transactionStatus.txId()} rollback!")
                })
            } else {
              activityTM.commit(transactionStatus)
              logger.info(s"tx:${transactionStatus.txId()} commit!")
            }
            result
          } catch {
            case ex: Throwable => {
              activityTM.rollback(transactionStatus)
              logger.info(s"tx:${transactionStatus.txId()} rollback!")
              throw ex
            }
          } finally {
            TXIdLocal.clear_txId
          }
        )
      }

      case action:ActionAnnotationAttribute => {
        action.parseParamValue(invocation.getArguments)
        //action begin
        val transactionStatus = actionTM.begin(action)
        val result = invocation.proceed()
        if(result.isInstanceOf[Future[_]]) {
          result.asInstanceOf[Future[_]]
            .onSuccess(r => {
              actionTM.prepare(transactionStatus.txId())
              logger.info(s"action:${transactionStatus.txId()} prepare success")
            })
        } else {
          actionTM.prepare(transactionStatus.txId())
          logger.info(s"action:${transactionStatus.txId()} prepare success")
        }
        result
      }

      case _ => invocation.proceed()

    }
  }

}
