package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.annotation.Annotation
import java.lang.reflect.{Method, AnnotatedElement}

import com.itiancai.galaxy.dts.util.ExtendedBeanUtils
import org.apache.commons.lang.StringUtils
import org.springframework.core.MethodParameter


class ParamAnnotationParse {

   def parseTransactionAnnotation(annotatedElement: AnnotatedElement): ParamAnnotationAttribute = {
    if (!annotatedElement.isInstanceOf[Method]) {
       null
    }
    val handlerMethod = annotatedElement.asInstanceOf[Method]
    val paramTypes: Array[Class[_]] = handlerMethod.getParameterTypes
    val args: Array[AnyRef] = new Array[AnyRef](paramTypes.length)

    var annotationsFound = 0
    var annotationValue: String = null
    var paramIndex = 0

    args.toList.zipWithIndex.foreach({ case (arg, i) =>
      val methodParam: MethodParameter = new MethodParameter(handlerMethod, i)
      //GenericTypeResolver.resolveParameterType(methodParam, handler.getClass)
      var paramName: String = null
      val paramAnns: Array[Annotation] = methodParam.getParameterAnnotations


      for (paramAnn <- paramAnns) {
        if (classOf[Param].isInstance(paramAnn)) {
          val param = paramAnn.asInstanceOf[Param]
          annotationValue = param.value()
          paramIndex = i
          annotationsFound += 1
        }
      }

      if (annotationsFound > 1) {
        //TODO抛出异常
      }
    })

    if (annotationsFound == 0) {
      //TODO抛出异常
    }

    ParamAnnotationAttribute(paramIndex, annotationValue)
  }

}

case class ParamAnnotationAttribute(index: Int, propertyValue: String) {
  def value(args: Array[AnyRef]): String = {
    if (StringUtils.isNotBlank(propertyValue))
      ExtendedBeanUtils.getProperty(args(index), propertyValue)
    else
      args(index).toString
  }
}
