package com.itiancai.galaxy.dts.config

object TransactionManagementConfigUtils {

  final val TRANSACTION_MANAGER_BEAN = "com.itiancai.galaxy.dts.support.activityTransactionManager"

  final val RESOURCE_MANAGER_BEAN = "com.itiancai.galaxy.dts.support.resourceManager"

  final val ANNOTATION_TRAN_ATTRIBUTE_SOURCE_BEAN = "com.itiancai.galaxy.dts.annotation.annotationTransactionAttributeSource"

  final val TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR_BEAN = "com.itiancai.galaxy.dts.interceptor.beanFactoryTransactionAttributeSourceAdvisor"

  final val TRANSACTION_INTERCEPTOR_BEAN = "com.itiancai.galaxy.dts.interceptor.transactionInterceptor"

  final val JDBC_TEMPLATE_BEAN = "com.itiancai.galaxy.dts.inner."

}
