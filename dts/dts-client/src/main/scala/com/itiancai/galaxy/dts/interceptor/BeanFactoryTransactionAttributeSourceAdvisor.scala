package com.itiancai.galaxy.dts.interceptor

import javax.inject.Inject

import com.itiancai.galaxy.dts.recovery.RecoveryClientSource
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
import org.springframework.stereotype.Component


@Component
class BeanFactoryTransactionAttributeSourceAdvisor @Inject()
  (transactionAttributeSource: TransactionAttributeSource, recoveryClientSource: RecoveryClientSource)
    extends AbstractBeanFactoryPointcutAdvisor {

  val pointcut: Pointcut = new TransactionAttributeSourcePointcut {
    override def getTransactionAttributeSource: TransactionAttributeSource = transactionAttributeSource

    override def getRecoveryClientSource: RecoveryClientSource = recoveryClientSource
  }

  override def getPointcut: Pointcut = pointcut
}
