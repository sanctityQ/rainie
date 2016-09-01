package com.itiancai.galaxy.dts;

//import com.itiancai.galaxy.dts.utils.RecoveryClientFactory;
import com.itiancai.galaxy.dts.recovery.RecoveryClientSource;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Future;

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
  public static RecoveryClientSource recoveryClientSource() {
    return null;
  }
}
