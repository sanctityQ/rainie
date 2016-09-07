package com.itiancai.galaxy.dts;


import com.itiancai.dts.client.recovery.ActivityStateResolverAImpl;
import com.itiancai.galaxy.dts.annotation.Action;
import com.itiancai.galaxy.dts.annotation.Activity;
import com.itiancai.galaxy.dts.annotation.Param;
import org.springframework.stereotype.Component;

@Component
public class TestJavaController {
    private TestJavaController colltroller;

    public void set(Object obj) {
        colltroller = (TestJavaController) obj;
    }

    @Activity(businessType = ActivityStateResolverAImpl.class)
    public String testSuccessActivity(String name, @Param String id, String password) {
        colltroller.testSuccessAction(name, id, password);
        return "success";
    }

    @Action(name = "p2p:lending:name")
    public void testSuccessAction(String name, @Param String id, String password) {
    }

    @Activity(businessType = ActivityStateResolverAImpl.class)
    public String testFailActivity(String name, String id, String password) {
        colltroller.testFailAction(name, id, password);
        return "success";
    }

    @Action(name = "p2p:lending:name")
    public void testFailAction(String name, @Param String id, String password) {
    }

    @Activity(businessType = ActivityStateResolverAImpl.class)
    public String testFailAcActivity(String name, @Param String id, String password) {
        colltroller.testFailAction2(name, id, password);
        return "success";
    }

    @Action(name = "p2p:lending:name")
    public void testFailAction2(String name, String id, String password) {
    }

    @Activity(businessType = ActivityStateResolverAImpl.class)
    public String testSuccessJsonAcvity(String name, @Param String id, String password) {
//        com.itiancai.galaxy.dts.domain.Action action = new com.itiancai.galaxy.dts.domain.Action();
//        action.setActionId("ddd");
//        action.setContext("ddd");
//        action.setId(1l);
//        colltroller.testSuccessJsonAction(action, "jsonTest");
        return "success";
    }

//    @Action(name = "p2p:lending:name")
//    public void testSuccessJsonAction(com.itiancai.galaxy.dts.domain.Action action, @Param String id) {
//    }

}
