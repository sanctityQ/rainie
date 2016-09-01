package com.itiancai.galaxy.dts.interceptor.annotation

import java.lang.reflect.Method

import com.itiancai.galaxy.dts.interceptor.{TransactionAttribute, AbstractFallbackTransactionAttributeSource}
import org.springframework.stereotype.Component

import scala.collection.immutable.HashSet


@Component
class AnnotationTransactionAttributeSource extends AbstractFallbackTransactionAttributeSource {

  val annotationParsers: Set[TransactionAnnotationParser] =
      HashSet[TransactionAnnotationParser](ActivityAnnotationParse, ActionAnnotationParse)

  override def findTransactionAttribute(specificMethod: Method): TransactionAttribute = {
    annotationParsers.foreach(annotationParser => {
      val attr = annotationParser.parseTransactionAnnotation(specificMethod)
      if (attr.isDefined)
        attr
    })
    null
  }


}
