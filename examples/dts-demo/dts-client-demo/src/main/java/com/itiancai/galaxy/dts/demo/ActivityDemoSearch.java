package com.itiancai.galaxy.dts.demo;


import com.itiancai.galaxy.dts.ActivityState;
import org.springframework.stereotype.Component;

@Component("activitySearch")
public class ActivityDemoSearch implements ActivityState {

    @Override
    public int isDone(String businessId) {
        return 0;
    }
    @Override
    public String name() {
        return "activitySearch";
    }
}
