package com.itiancai.passport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.itiancai.passport")
public class SpringBoot {

  @Value("server.port")
  private String serverPort;

}
