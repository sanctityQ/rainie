package com.itiancai.galaxy.dts.annotation

import java.lang.reflect.{AnnotatedElement, Method}

import org.springframework.core.MethodParameter




class ParamAnnotationParse {

   def parseTransactionAnnotation(annotatedElement: AnnotatedElement): ParamAnnotationAttribute = {
    if (!annotatedElement.isInstanceOf[Method]) {
       return null
    }

     val handlerMethod = annotatedElement.asInstanceOf[Method]
     val paramTypes = handlerMethod.getParameterTypes

     val list = for (
       i <- 0 until paramTypes.length;
       methodParam = new MethodParameter(handlerMethod, i);
       paramAnns = methodParam.getParameterAnnotations;
       if (paramAnns.exists(_.isInstanceOf[Param]))
     ) yield {
       val paramList = paramAnns.filter(_.isInstanceOf[Param])
       if (paramList.size > 1) throw new RuntimeException("param Annotation only one")
       (i, paramList(0).asInstanceOf[Param])
     }

     if (list.size != 1) throw new RuntimeException("param Annotation only one")

     ParamAnnotationAttribute(list(0)._1, list(0)._2.parse())
  }

}

case class ParamAnnotationAttribute(index: Int, propertyValue: Class[_ <: ParamParser]) {
  def value(args: Array[AnyRef]): String = {
      propertyValue.newInstance().parse(args(index))
  }
}
