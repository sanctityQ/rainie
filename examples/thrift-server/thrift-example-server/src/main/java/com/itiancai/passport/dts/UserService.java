package com.itiancai.passport.dts;

import com.itiancai.galaxy.dts.annotation.Activity;
import com.itiancai.galaxy.dts.annotation.Param;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Activity(businessType = ActivityStateResolverAImpl.class)
    public void test1(@Param String id){}
}
