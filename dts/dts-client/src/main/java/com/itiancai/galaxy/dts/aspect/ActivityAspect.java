package com.itiancai.galaxy.dts.aspect;

import com.itiancai.galaxy.dts.DTSController;
import com.itiancai.galaxy.dts.annotation.Activity;
import com.itiancai.galaxy.dts.domain.Status;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsp on 16/7/28.
 *
 * 主事务切入点
 */

@Aspect
@Component
public class ActivityAspect {


    @Autowired
    DTSController dtsManager;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.itiancai.galaxy.dts.annotation.Activity)")
    public  void activityAspect() {}


    /**
     * 切点处理befor after throw 三种情况
     *
     * @param joinPoint
     * @throws Exception
     */
    @Around("activityAspect()")
    public  void doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String,Object> mapPra = getServiceMethodDescription(joinPoint);
        String businessId =  mapPra.get("businessId").toString();
        String businessType =  mapPra.get("businessType").toString();
        long timeOut =  Long.parseLong(mapPra.get("timeOut").toString());
        dtsManager.startActivity(businessId,businessType,timeOut);

        //TODO 需考虑不是使用finagle架构时,tx_id使用finagle 做域限制,不使用时完成后clear tx_id
        Object object = joinPoint.proceed();

        Boolean isImmediately =  Boolean.parseBoolean(mapPra.get("isImmediately").toString());
        dtsManager.finishActivity(isImmediately, Status.Activity.SUCCESS);

    }
    //TODO 定义异常类
    @AfterThrowing("activityAspect()")
    public void doThrowingException(ProceedingJoinPoint joinPoint){
//        dtsManager.finishActivity(isImmediately, Status.Activity.SUCCESS);
    }

    /**
     * 获取注解中对方法的描述信息及方法信息 TODO 需要对businessId,businessType为空进行验证 如为空抛出异常
     *
     * @param joinPoint 切点
     * @return Map<String,Object></>
     * @throws Exception
     */
    public  static Map<String,Object> getServiceMethodDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        Map<String,Object> map = new HashMap<String, Object>();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    map.put("businessId",method.getAnnotation(Activity.class).businessId());
                    map.put("businessType",method.getAnnotation(Activity.class).businessType());
                    map.put("isImmediately", method.getAnnotation(Activity.class).isImmediately());
                    map.put("timeOut", method.getAnnotation(Activity.class).timeOut());
                }
            }
        }
        return map;
    }


}
