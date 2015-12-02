package com.itiancai.galaxy.inject

import com.itiancai.galaxy.inject.config.ConfigureEnvironment
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, AnnotationConfigApplicationContext}
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

import scala.collection.mutable.ArrayBuffer
import scala.reflect._


private[inject] object ContextHolder {

  private[this] val environment = new ConfigureEnvironment()

  private[this] val appContext: AnnotationConfigApplicationContext = {
    val appContext = new AnnotationConfigApplicationContext()
    appContext.setEnvironment(environment)
    appContext
  }

  private[this] val binder_ = Binder(appContext.getBeanFactory)
  private[this] val injector_ = Injector(appContext.getBeanFactory)

  binder_ bind injector_

  private[inject] def injector(annotationClasses: Seq[Class[_]], modules: Seq[Module]): Injector = {
    appContext.register(annotationClasses: _*)
    modules foreach (module => module.config(binder_))
    appContext.refresh()
    injector_
  }

}

case class Binder(beanFactory: ConfigurableBeanFactory) {
  def bind(anyRef: AnyRef): Unit = {
    beanFactory.registerSingleton(anyRef.getClass.getCanonicalName, anyRef)
  }
}

case class Injector(underlying: BeanFactory) {

  def instance[T: ClassTag]: T = underlying.getBean(classTag[T].runtimeClass.asInstanceOf[Class[T]]);

  //  def instance[T: Manifest, Ann <: JavaAnnotation : Manifest]: T = {
  //    val annotationType = manifest[Ann].runtimeClass.asInstanceOf[Class[Ann]]
  //    val key = Key.get(typeLiteral[T], annotationType)
  //    underlying.getInstance(key)
  //  }

  def instance[T: Manifest](name: String): T = {
    underlying.getBean(name).asInstanceOf[T]
  }

  def instance[T](clazz: Class[T]): T = underlying.getBean(clazz)

  // def instance[T](key: Key[T]): T = underlying.getInstance(key)
}

@Configuration
@ComponentScan(Array("com.itiancai.galaxy"))
private[this] class InjectContextConfig {

  @Bean
  def getPropertySourcesPlaceholderConfigurer = {
    new PropertySourcesPlaceholderConfigurer
  }

}


trait ContextConfig {

  private val registerAnnotationClasses: ArrayBuffer[ClassTag[_]] = ArrayBuffer(classTag[InjectContextConfig])

  def registerClass(): Seq[Class[_]] = {
    registerAnnotationClasses.toSeq map (cl => cl.runtimeClass)
  }

  def addAnnotationClass[T: ClassTag] = {
    registerAnnotationClasses += classTag[T]
    this
  }

}



