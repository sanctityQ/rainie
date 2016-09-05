package com.itiancai.galaxy.dts.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    Class<? extends ParamParser> parse() default DefaultParser.class;
}
