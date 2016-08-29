package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.reflect.Method

import com.itiancai.galaxy.dts.ExtendedBeanUtils
import org.springframework.core.annotation.AnnotationUtils

class ActivityAnnotationParse() {
  def apply(handlerMethod: Method): ParseActivityAnnotation = {
    val activity = AnnotationUtils.getAnnotation(handlerMethod, classOf[Activity])
    ParseActivityAnnotation(activity.businessType, activity.timeOut, activity.isImmediately, ParamAnnotation(handlerMethod))
  }

  def businessId(args: Array[AnyRef]) = {
    if (this.param.value.length != 0) {
      ExtendedBeanUtils.getProperty(args(this.param.index), this.param.value)
    } else {
      args(this.param.index).toString
    }
  }
}
