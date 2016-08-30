package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.reflect.{AnnotatedElement, Method}
import java.util.{Map => JMap}

import com.itiancai.galaxy.dts.ExtendedBeanUtils
import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.recovery.ActivityStateResolver
import org.springframework.beans.BeanUtils
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.`type`.MethodMetadata
import org.springframework.core.annotation.{AnnotationAttributes, AnnotationUtils}
import org.springframework.transaction.annotation.Transactional


object ActivityAnnotationParse extends TransactionAnnotationParser {

  val paramAnnotationParse: ParamAnnotationParse = new ParamAnnotationParse

  def apply(metadata: MethodMetadata):Unit = {

    val attributes: JMap[String, AnyRef] = metadata.getAnnotationAttributes(classOf[Activity].getName)

    if(attributes == null)
      return
    if (attributes.eq(None)) {
      None
    }

  }

  override def parseTransactionAnnotation(annotatedElement: AnnotatedElement): TransactionAttribute = {
    val ann: Activity = AnnotationUtils.getAnnotation(annotatedElement, classOf[Activity])
    val activityAnno = BeanUtils.instantiateClass(ann.businessType(), classOf[ActivityStateResolver])
    ActivityAnnotationAttribute(activityAnno.name(), ann.timeOut(), ann.isImmediately,
      paramAnnotationParse.parseTransactionAnnotation(annotatedElement))
  }
}

//class ActivityConfig(annotationAttributes: AnnotationAttributes) {
//  val businessType = annotationAttributes.getClass[ActivityStateResolver]("businessType")
//  val timeOut = annotationAttributes.getNumber("timeOut")
//  val isImmediately = annotationAttributes.getBoolean("isImmediately")
//}


case class ActivityAnnotationAttribute(businessType: String, timeOut: Int, isImmediately: Boolean,
                                   param: ParamAnnotationAttribute) extends TransactionAttribute{

  override def name(): String = businessType

  def value(args: Array[AnyRef]): String = {
    param.value(args)
  }

}


