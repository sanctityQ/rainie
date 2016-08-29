package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.annotation.Annotation
import java.lang.reflect.Method

import com.itiancai.galaxy.dts.ExtendedBeanUtils
import org.apache.commons.lang.StringUtils
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotationUtils


object ParseActionAnnotation {
  def apply(handlerMethod: Method): ParseActionAnnotation = {
    val action = AnnotationUtils.getAnnotation(handlerMethod, classOf[Action])
    ParseActionAnnotation(action.name(), ParamAnnotation(handlerMethod))
  }
}

case class ParseActionAnnotation(name: String, param: ParamAnnotation) {
  def instructionId(args: Array[AnyRef]) = {
    if (StringUtils.isNotBlank(param.value))
      ExtendedBeanUtils.getProperty(args(param.index), param.value)
    else
      args(param.index).toString
  }
}

object ParseActivityAnnotation {
  def apply(handlerMethod: Method): ParseActivityAnnotation = {
    val activity = AnnotationUtils.getAnnotation(handlerMethod, classOf[Activity])
    ParseActivityAnnotation(activity.businessType, activity.timeOut, activity.isImmediately, ParamAnnotation(handlerMethod))
  }
}

case class ParseActivityAnnotation(businessType: String, timeOut: Int, isImmediately: Boolean,
                                   param: ParamAnnotation) {
  def businessId(args: Array[AnyRef]) = {
    if (this.param.value.length != 0) {
      ExtendedBeanUtils.getProperty(args(this.param.index), this.param.value)
    } else {
      args(this.param.index).toString
    }
  }

}

object ParamAnnotation {
  def apply(handlerMethod: Method): ParamAnnotation = {
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

    ParamAnnotation(paramIndex, annotationValue)

  }
}

case class ParamAnnotation(index: Int, value: String)




