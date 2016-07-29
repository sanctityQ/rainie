package com.itiancai.dts.test.client;

import com.itiancai.galaxy.dts.annotation.Action;
import com.itiancai.galaxy.dts.annotation.Activity;

import java.lang.reflect.Method;

/**
 * Created by lsp on 16/7/30.
 */
public class TestAnnotation {

    @Activity(businessId = "lsp",isImmediately = false)
    public void testAnn(String dd){
        System.out.println("dddd");
    }
    public static void main(String []args){
//        TestAnnotation testAnnotation = new TestAnnotation();
//        testAnnotation.testAnn("dds");
//        Method[] methods = testAnnotation.getClass().getMethods();
//        for (Method method : methods) {
//            Activity activity = method.getAnnotation(Activity.class);
//            System.out.println(activity.);
//            if (method.getName().equals("testAnn")) {
//                System.out.println(method.getAnnotation(Activity.class).businessId());
//                System.out.println(method.getAnnotation(Activity.class).isImmediately());
//            }
//        }
        int i = 1/0;
    }
}
