package com.itiancai.galaxy.dts.aspect;

import com.itiancai.galaxy.dts.DTSController;
import com.itiancai.galaxy.dts.annotation.Action;
import com.itiancai.galaxy.dts.domain.Status;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsp on 16/7/28.
 *
 * 子事务切入点
 */
@Aspect
@Component
public class ActionAspect{

    @Autowired
    DTSController dtsManager;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.itiancai.galaxy.dts.annotation.Action)")
    public  void actionAspect() {}

    /**
     * 切点处理befor after 两种情况
     *
     * @param joinPoint
     * @throws Exception
     */
    @Around("actionAspect()")
    public void doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String,String> mapPra = getServiceMethodDescription(joinPoint);
        String instructionId =  mapPra.get("businessId");
        String name =  mapPra.get("businessType");
        String context = joinPoint.getArgs().toString();
        dtsManager.startAction(instructionId,name,context);
        joinPoint.proceed();
        dtsManager.prepareAction(Status.Action.SUCCESS);
    }

    /**
     * 切点处理throwing 情况
     *
     * @param joinPoint
     */
    @AfterThrowing("actionAspect()")
    public void doThrowingException(ProceedingJoinPoint joinPoint){

    }

    /**
     * 获取注解中对方法的描述信息及方法信息 TODO 需要对businessId,businessType为空进行验证 如为空抛出异常
     *
     * @param joinPoint 切点
     * @return Map<String,Object></>
     * @throws Exception
     */
    public  static Map<String,String> getServiceMethodDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        Map<String,String> map = new HashMap<String, String>();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    map.put("instructionId",method.getAnnotation(Action.class).instructionId());
                    map.put("name",method.getAnnotation(Action.class).name());
                }
            }
        }
        return map;
    }
}
