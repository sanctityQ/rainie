package com.itiancai.galaxy.dts;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by bao on 15/11/17.
 */

@Configuration
//@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
//@PropertySource({"classpath:/config/${galaxias.env}/server.properties"})
@ImportResource({"applicationContext-dts.xml"})
public class SpringBoot {

}
