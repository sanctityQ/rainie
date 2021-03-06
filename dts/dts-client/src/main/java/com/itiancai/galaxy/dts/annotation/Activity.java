package com.itiancai.galaxy.dts.annotation;

import com.itiancai.galaxy.dts.ActivityState;

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
    Class<? extends ActivityState> businessType();
}
