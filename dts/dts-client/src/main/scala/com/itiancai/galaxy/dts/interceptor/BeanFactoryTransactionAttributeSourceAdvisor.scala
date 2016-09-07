package com.itiancai.galaxy.dts.interceptor

import javax.inject.Inject
import com.itiancai.galaxy.dts.config.TransactionManagementConfigUtils
import com.itiancai.galaxy.dts.http.HttpClientSource
import org.aopalliance.aop.Advice
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
import org.springframework.beans.factory.annotation.{Qualifier, Autowired}
import org.springframework.stereotype.Component


@Component(TransactionManagementConfigUtils.TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR_BEAN)
class BeanFactoryTransactionAttributeSourceAdvisor @Inject()
  (transactionAttributeSource: TransactionAttributeSource, recoveryClientSource: HttpClientSource)
    extends AbstractBeanFactoryPointcutAdvisor {

  val pointcut: Pointcut = new TransactionAttributeSourcePointcut {
    override def getTransactionAttributeSource: TransactionAttributeSource = transactionAttributeSource

    override def getRecoveryClientSource: HttpClientSource = recoveryClientSource
  }

  override def getPointcut: Pointcut = pointcut

  @Autowired
  @Qualifier(TransactionManagementConfigUtils.TRANSACTION_INTERCEPTOR_BEAN)
  override def setAdvice(advice: Advice){
    super.setAdvice(advice)
  }
}
