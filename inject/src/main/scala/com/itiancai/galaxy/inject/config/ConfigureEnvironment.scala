package com.itiancai.galaxy.inject.config

import java.io.IOException

import org.springframework.core.env.{SystemEnvironmentPropertySource, MapPropertySource, MutablePropertySources, StandardEnvironment}
import org.springframework.core.io.support.{ResourcePropertySource, PathMatchingResourcePatternResolver, ResourcePatternResolver}
import org.springframework.util.Assert

import scala.reflect.io.{File, Path}


class ConfigureEnvironment(env: com.twitter.app.Flag[String], configPath: com.twitter.app.Flag[String]) extends StandardEnvironment {


  override def customizePropertySources( propertySources: MutablePropertySources) {


    super.customizePropertySources(propertySources)

    val resourceSourcePath = configPath() + File.separator + env() + File.separator + "**" + File.separator + "*.properties"

//    val resourceSourcePath = this.resolvePlaceholders(configPath());
    val envPropertySources:MutablePropertySources = this.getPropertySources();

    val resolver:ResourcePatternResolver = new PathMatchingResourcePatternResolver();
    try {
      val resources = resolver.getResources(resourceSourcePath);
      for (resource <-resources) {
        envPropertySources.addLast(new ResourcePropertySource(resource));
      }
    } catch {
      case e: IOException => logger.error("load config resource failure, path: [" + resourceSourcePath + "]", e);
      throw new RuntimeException(e);
    }
    logger.info("load config resource successful, path : [" + resourceSourcePath + "]");

  }

}
