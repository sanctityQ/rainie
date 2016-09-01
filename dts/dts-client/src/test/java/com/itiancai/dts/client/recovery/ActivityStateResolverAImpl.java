package com.itiancai.dts.client.recovery;

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
        return "p2p:lending:name";
    }
}
