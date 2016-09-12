package com.itiancai.passport.dts;

import com.itiancai.galaxy.dts.ActivityState;
import org.springframework.stereotype.Component;

@Component
public class ActivityStateResolverAImpl implements ActivityState {

    @Override
    public int isDone(String businessId) {
        return 0;
    }

    @Override
    public String name() {
        return "p2p:user:test1";
    }
}
