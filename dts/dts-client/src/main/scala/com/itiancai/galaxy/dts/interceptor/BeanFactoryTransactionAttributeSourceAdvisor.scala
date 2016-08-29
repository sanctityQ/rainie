package com.itiancai.galaxy.dts.interceptor

import com.itiancai.galaxy.dts.interceptor.annotation.AnnotationTransactionAttributeSource
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor


class BeanFactoryTransactionAttributeSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor{

  val pointcut: Pointcut = new TransactionAttributeSourcePointcut {
    override  def getTransactionAttributeSource: TransactionAttributeSource =  new AnnotationTransactionAttributeSource
  }

  override def getPointcut: Pointcut = pointcut
}
