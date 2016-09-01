package com.itiancai.galaxy.dts.interceptor

import javax.inject.Inject

import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
import org.springframework.stereotype.Component


@Component
class BeanFactoryTransactionAttributeSourceAdvisor @Inject()(transactionAttributeSource: TransactionAttributeSource)
    extends AbstractBeanFactoryPointcutAdvisor{

  val pointcut: Pointcut = new TransactionAttributeSourcePointcut {
    override  def getTransactionAttributeSource: TransactionAttributeSource = transactionAttributeSource
  }

  override def getPointcut: Pointcut = pointcut
}
