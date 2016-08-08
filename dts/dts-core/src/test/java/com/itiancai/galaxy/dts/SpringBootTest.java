package com.itiancai.galaxy.dts;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan({"com.itiancai.galaxy.dts"})
@PropertySource({"classpath:server.properties"})
@Import({DTSDatasource.class})
public class SpringBootTest {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}