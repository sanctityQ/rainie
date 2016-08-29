package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.reflect.AnnotatedElement

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import org.springframework.core.annotation.AnnotationUtils

object ActionAnnotationParse extends TransactionAnnotationParser {

  val paramAnnotationParse: ParamAnnotationParse = new ParamAnnotationParse

  override def parseTransactionAnnotation(annotatedElement: AnnotatedElement): TransactionAttribute = {

    val action = AnnotationUtils.getAnnotation(annotatedElement, classOf[Action])

    ActionAnnotationAttribute(action.name(), paramAnnotationParse.parseTransactionAnnotation(annotatedElement))

  }

}


case class ActionAnnotationAttribute(name: String, param: ParamAnnotationAttribute) extends TransactionAttribute {

  def value(args: Array[AnyRef]): String = {

    param.value(args)

  }

}
