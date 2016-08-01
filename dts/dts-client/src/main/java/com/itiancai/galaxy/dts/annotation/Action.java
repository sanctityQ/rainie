package com.itiancai.galaxy.dts.annotation;

import java.lang.annotation.*;

/**
 * Created by lsp on 16/7/28.
 *
 * 子事务注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

    /**
     * 幂等参数名称
     * @return string
     */
    String instructionId() default "";

    /**
     * 服务名称,唯一值(例如:系统名.模块名.服务名)
     * @return string
     */
    String name() default "";
}
