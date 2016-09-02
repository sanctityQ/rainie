package com.itiancai.passport.dts;

import com.itiancai.galaxy.dts.recovery.ActivityStateResolver;
import org.springframework.stereotype.Component;

@Component
public class ActivityStateResolverAImpl implements ActivityStateResolver {

    @Override
    public int isDone(String businessId) {
        return 0;
    }

    @Override
    public String name() {
        return "p2p:user:test1";
    }
}
