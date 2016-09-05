package com.itiancai.galaxy.dts.action.demo;


import com.itiancai.galaxy.dts.XAResourceActionService;
import org.springframework.stereotype.Component;

@Component("actionDemoDone")
public class ActionDemoDone implements XAResourceActionService {
    @Override
    public boolean commit(String instructionId) {
        return true;
    }

    @Override
    public boolean rollback(String instructionId) {
        return true;
    }

    @Override
    public String name() {
        return "actionDemoDone";
    }
}
