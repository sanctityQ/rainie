package com.itiancai.galaxy.dts;

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
//@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
@PropertySource({"classpath:/config/test/server.properties"})
@Import({DTSDatasource.class})
public class SpringBootTest {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
