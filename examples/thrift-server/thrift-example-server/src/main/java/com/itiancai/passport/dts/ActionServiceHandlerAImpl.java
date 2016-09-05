package com.itiancai.passport.dts;

import com.itiancai.galaxy.dts.XAResourceActionService;
import org.springframework.stereotype.Component;

@Component
public class ActionServiceHandlerAImpl implements XAResourceActionService {

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
        return "actionA";
    }
}
