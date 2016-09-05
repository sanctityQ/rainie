package com.itiancai.galaxy.dts.annotation

import java.lang.reflect.AnnotatedElement

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.itiancai.galaxy.dts.support.ServiceName
import org.springframework.core.annotation.AnnotationUtils

object ActionAnnotationParse extends TransactionAnnotationParser {

  val paramAnnotationParse: ParamAnnotationParse = new ParamAnnotationParse

  override def parseTransactionAnnotation(annotatedElement: AnnotatedElement): Option[TransactionAttribute] = {

    val action = AnnotationUtils.getAnnotation(annotatedElement, classOf[Action])
    if(action == null)
      return None
    Some(ActionAnnotationAttribute(action.name(), paramAnnotationParse.parseTransactionAnnotation(annotatedElement)))

  }

}


case class ActionAnnotationAttribute(name_ : String, param: ParamAnnotationAttribute) extends TransactionAttribute {

  var value_ : Option[String] = None

  def parseParamValue(params: Array[AnyRef]) = {
    this.value_ = Option(param.value(params))
  }

  override def paramValue(): String = value_.get

  override def timeOut(): Int = 0

  override def name(): ServiceName = ServiceName(name_)
}
