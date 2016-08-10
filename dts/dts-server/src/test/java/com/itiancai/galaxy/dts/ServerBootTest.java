package com.itiancai.galaxy.dts;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by bao on 15/11/17.
 */

@Configuration
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
@PropertySource({"classpath:/config/test/server.properties"})
@Import({DTSDatasource.class, SpringBoot.class})
public class ServerBootTest {

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
