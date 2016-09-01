package com.itiancai.galaxy.dts.annotation

import java.lang.reflect.AnnotatedElement

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute


trait TransactionAnnotationParser {



  def parseTransactionAnnotation(annotatedElement: AnnotatedElement): Option[TransactionAttribute]

}
