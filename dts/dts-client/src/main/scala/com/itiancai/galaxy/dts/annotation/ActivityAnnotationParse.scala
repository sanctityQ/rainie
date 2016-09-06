package com.itiancai.galaxy.dts.annotation

import java.lang.reflect.AnnotatedElement
import java.util.{Map => JMap}
import com.itiancai.galaxy.dts.ActivityState
import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.support.ServiceName
import org.springframework.beans.BeanUtils
import org.springframework.core.annotation.AnnotationUtils


object ActivityAnnotationParse extends TransactionAnnotationParser {

  val paramAnnotationParse: ParamAnnotationParse = new ParamAnnotationParse


  override def parseTransactionAnnotation(annotatedElement: AnnotatedElement): Option[TransactionAttribute] = {
    val ann: Activity = AnnotationUtils.getAnnotation(annotatedElement, classOf[Activity])
    if(ann == null)
      return None
    val activityAnno = BeanUtils.instantiateClass(ann.businessType(), classOf[ActivityState])
    Some(ActivityAnnotationAttribute(activityAnno.name(), ann.timeOut(), ann.isImmediately,
      paramAnnotationParse.parseTransactionAnnotation(annotatedElement)))
  }
}


case class ActivityAnnotationAttribute(businessType: String, timeOut_ : Int, isImmediately: Boolean,
                                   param: ParamAnnotationAttribute) extends TransactionAttribute {

  override def  name(): ServiceName = ServiceName(businessType)

  //default set null
  var value_ : Option[String] = None

  private[dts] def parseParamValue(params: Array[AnyRef]) = {
    this.value_ = Option(param.value(params))
  }

  def paramValue() : String = {
    this.value_.get
  }

  override def timeOut(): Int = timeOut_
}



