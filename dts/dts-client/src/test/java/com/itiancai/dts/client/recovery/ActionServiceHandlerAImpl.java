package com.itiancai.dts.client.recovery;

import com.itiancai.galaxy.dts.recovery.ActionServiceHandler;
import org.springframework.stereotype.Component;

@Component
public class ActionServiceHandlerAImpl implements ActionServiceHandler{



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
