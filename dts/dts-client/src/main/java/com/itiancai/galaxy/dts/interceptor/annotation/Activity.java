package com.itiancai.galaxy.dts.interceptor.annotation;

import com.itiancai.galaxy.dts.recovery.ActivityStateResolver;

import java.lang.annotation.*;

/**
 * 主事务注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Activity {

    /**
     * 子事务是否实时commit或rollback
     * @return true-实时处理,false-补偿服务处理
     */
    boolean isImmediately() default true;

    /**
     * 服务名称,唯一值(例如 p2p:lending:activityType)
     * @return string
     */
    Class<? extends ActivityStateResolver> businessType();

    /**
     * 主线程超时时间 单位:毫秒
     * @return
     */
    int timeOut() default 3000;
}
