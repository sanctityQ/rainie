package com.itiancai.galaxy.dts;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
@PropertySource({"classpath:/config/test/server.properties"})
public class SpringBootTest {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
