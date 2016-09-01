package com.itiancai.galaxy.dts;

import com.itiancai.galaxy.dts.boot.TXBoot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
@Import(TXBoot.class)
public class SpringBoot {

}
