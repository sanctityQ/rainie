package com.itiancai.galaxy.dts.config;


import com.google.common.collect.Maps;
import com.itiancai.galaxy.dts.ActivityState;
import com.itiancai.galaxy.dts.XAResourceActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActivityStateAndXAResourceActionSource implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ActivityStateAndXAResourceActionSource.class);

    private Map<String, ActivityState> activityStateResolverHashMap = Maps.newHashMap();

    private Map<String, XAResourceActionService> actionServiceHandlerMap = Maps.newHashMap();

    private ListableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext;
    }

    public ActivityState getActivityStateResolver(String name) {
        if (activityStateResolverHashMap.isEmpty()) {
            init();
        }
        return this.activityStateResolverHashMap.get(name);
    }

    public boolean exists(String type, String name) {
        init();
        if(type.equals("action")){
            return actionServiceHandlerMap.containsKey(name);
        }

        if(type.equals("activity")){
            return activityStateResolverHashMap.containsKey(name);
        }

        return false;
    }



    public XAResourceActionService getActionServiceHandler(String name) {
        if (actionServiceHandlerMap.isEmpty()) {
            init();
        }
        return this.actionServiceHandlerMap.get(name);
    }


    private void init() {

        Map<String, ActivityState> activityStateResolverMap = beanFactory.getBeansOfType(ActivityState.class);
        for (ActivityState activityStateResolver : activityStateResolverMap.values()) {
            logger.info("init ActivityStateResolver name is {}", activityStateResolver.name());
            this.activityStateResolverHashMap.put(activityStateResolver.name(), activityStateResolver);
        }

        Map<String, XAResourceActionService> actionServiceHandlerMap = beanFactory.getBeansOfType(XAResourceActionService.class);

        for (XAResourceActionService actionServiceHandler : actionServiceHandlerMap.values()) {
            logger.info("init actionServiceHandler name is {}", actionServiceHandler.name());
            this.actionServiceHandlerMap.put(actionServiceHandler.name(), actionServiceHandler);
        }


    }


}
