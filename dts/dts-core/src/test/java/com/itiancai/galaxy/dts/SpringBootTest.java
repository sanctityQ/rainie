package com.itiancai.galaxy.dts;

//import com.itiancai.galaxy.dts.utils.RecoveryClientFactory;
import com.itiancai.galaxy.dts.http.HttpClientSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by bao on 15/11/17.
 */

@Configuration
//@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
@PropertySource({"classpath:/config/test/server.properties"})
@ImportResource({"applicationContext-dts.xml"})
public class SpringBootTest {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public static HttpClientSource recoveryClientSource() {
    return null;
  }
}
