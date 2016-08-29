package com.itiancai.galaxy.dts.interceptor

import com.itiancai.galaxy.dts.interceptor.annotation.AnnotationTransactionAttributeSource
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor


class BeanFactoryTransactionAttributeSourceAdvisor(transactionAttributeSource: TransactionAttributeSource) extends AbstractBeanFactoryPointcutAdvisor{

  val pointcut: Pointcut = new TransactionAttributeSourcePointcut {
    override  def getTransactionAttributeSource: TransactionAttributeSource = transactionAttributeSource
  }

  override def getPointcut: Pointcut = pointcut
}
