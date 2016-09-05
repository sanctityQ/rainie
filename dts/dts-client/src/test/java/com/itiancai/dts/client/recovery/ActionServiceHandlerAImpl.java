package com.itiancai.dts.client.recovery;

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
