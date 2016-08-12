package com.itiancai.galaxy.dts.demo;


import com.itiancai.galaxy.dts.recovery.ActivityStateResolver;
import org.springframework.stereotype.Component;

@Component("activitySearch")
public class ActivityDemoSearch implements ActivityStateResolver {

    @Override
    public int isDone(String businessId) {
        return 0;
    }
    @Override
    public String name() {
        return "activitySearch";
    }
}
