package com.itiancai.galaxy.dts.annotation

import java.lang.reflect.AnnotatedElement
import java.util.{Map => JMap}

import com.itiancai.galaxy.dts.interceptor.{ExtendTransactionAttribute, TransactionAttribute}
import com.itiancai.galaxy.dts.recovery.ActivityStateResolver
import org.springframework.beans.BeanUtils
import org.springframework.core.`type`.MethodMetadata
import org.springframework.core.annotation.AnnotationUtils


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

  override def parseTransactionAnnotation(annotatedElement: AnnotatedElement): Option[TransactionAttribute] = {
    val ann: Activity = AnnotationUtils.getAnnotation(annotatedElement, classOf[Activity])
    if(ann == null)
      return None
    val activityAnno = BeanUtils.instantiateClass(ann.businessType(), classOf[ActivityStateResolver])
    Some(ActivityAnnotationAttribute(activityAnno.name(), ann.timeOut(), ann.isImmediately,
      paramAnnotationParse.parseTransactionAnnotation(annotatedElement)))
  }
}


case class ActivityAnnotationAttribute(businessType: String, timeOut: Int, isImmediately: Boolean,
                                   param: ParamAnnotationAttribute) extends TransactionAttribute with ExtendTransactionAttribute{

  override def name(): String = businessType

  //default set null
  var value_ : Option[String] = None

  private[dts] def parseParamValue(params: Array[AnyRef]) = {
    this.value_ = Option(param.value(params))
  }

  def paramValue() : String = {
    this.value_.get
  }

  override def timeOut_(): Int = timeOut
}



