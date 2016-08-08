package com.itiancai.galaxy.dts.annotation;

import java.lang.annotation.*;

/**
 * 子事务注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

    /**
     * 服务名称,唯一值(例如:系统名.模块名.服务名)
     * @return string
     */
    String name() default "";
}
