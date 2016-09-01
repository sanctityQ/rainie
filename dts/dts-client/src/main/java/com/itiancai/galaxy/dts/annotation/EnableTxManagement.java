package com.itiancai.galaxy.dts.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AbstractTxManagementConfiguration.class)
public @interface EnableTxManagement {

    String[] value() default {};

    String[] basePackages() default {};
}
