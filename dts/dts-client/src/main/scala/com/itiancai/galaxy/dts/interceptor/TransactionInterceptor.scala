package com.itiancai.galaxy.dts.interceptor

import com.itiancai.galaxy.dts.TransactionManager
import com.itiancai.galaxy.dts.interceptor.annotation.{ActionAnnotationAttribute, ActivityAnnotationAttribute}
import com.twitter.finagle.context.Contexts
import org.aopalliance.intercept.{MethodInvocation, MethodInterceptor}
import org.springframework.aop.support.AopUtils


class TransactionInterceptor(transactionAttributeSource: TransactionAttributeSource) extends MethodInterceptor with Serializable{

  val txId_key = new Contexts.local.Key[String]



  override def invoke(invocation: MethodInvocation): AnyRef = {

    val targetClass: Class[_] =  if (invocation.getThis != null) AopUtils.getTargetClass(invocation.getThis) else null

    val txAttr: TransactionAttribute = transactionAttributeSource.getTransactionAttribute(invocation.getMethod, targetClass)
    val tm: TransactionManager = determineTransactionManager(txAttr)



    txAttr match {

      case activity: ActivityAnnotationAttribute => {
        activity.value(invocation.getArguments)

        //transcation begin
        val transactionStatus = tm.begin(activity)
        //activityBegin
        Contexts.local.let(txId_key, transactionStatus.txId())(

          try {
            invocation.proceed()
          } catch {
            case ex: Throwable => {
              //activityRollback
              null
            }
          } finally {

          }

        )
      }

      case action:ActionAnnotationAttribute => {
        null
      }

      case _ => invocation.proceed()

    }
  }

  def determineTransactionManager(txAttr: TransactionAttribute): TransactionManager = {

  }



}
