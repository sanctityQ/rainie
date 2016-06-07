package com.itiancai.galaxy.inject

import com.itiancai.galaxy.inject.config.{ConfigureEnvironment2, ConfigureEnvironment}
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, AnnotationConfigApplicationContext}
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.io.support.ResourcePatternResolver

import com.twitter.app.{App => TwitterUtilApp}


import scala.collection.mutable.ArrayBuffer
import scala.reflect._


private[inject] object ContextHolder {

  private[this] val appContext: AnnotationConfigApplicationContext = new AnnotationConfigApplicationContext()

  private[this] val binder_ = Binder(appContext.getBeanFactory)
  private[this] val injector_ = Injector(appContext.getBeanFactory)

  binder_ bind injector_

  private[inject] def injector(contextConfig: ContextConfig, modules: Seq[Module]): Injector = {
    appContext.setEnvironment(contextConfig.environment)
    appContext.register(contextConfig.registerClass : _*)
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


trait ContextConfig extends TwitterUtilApp{

  private val registerAnnotationClasses: ArrayBuffer[ClassTag[_]] = ArrayBuffer(classTag[InjectContextConfig])


  val DEFAULT_ENV: String = "dev"

  val DEFAULT_ENV_LOCATION: String = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/config"

  val rainieEnv = flag("env", DEFAULT_ENV, "rainie current environment, default: dev")

  val configPath = flag("env.config", DEFAULT_ENV_LOCATION, "rainie environment config location path")

  def registerClass(): Seq[Class[_]] = {
    registerAnnotationClasses.toSeq map (cl => cl.runtimeClass)
  }

  def addAnnotationClass[T: ClassTag] = {
    registerAnnotationClasses += classTag[T]
    this
  }

  def environment: ConfigurableEnvironment = {
    new ConfigureEnvironment(rainieEnv, configPath)
  }




}



