package com.itiancai.galaxy.dts.demo.boot;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = "com.itiancai.galaxy.dts.demo")
public class SpringBoot {
  private static AnnotationConfigApplicationContext context;
  protected static void init() {
    context = new AnnotationConfigApplicationContext();
    context.register(SpringBoot.class);
    context.refresh();
  }
  public static final AnnotationConfigApplicationContext context() {
    if (context == null) {
      init();
    }
    return context;
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
