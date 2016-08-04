package com.itiancai.galaxy.dts.dts.test.client;

import com.itiancai.galaxy.dts.DTSController;
import com.itiancai.galaxy.dts.DatasourceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;


@ContextConfiguration(classes = {DatasourceTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class AspectTest  extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
     private DTSController  controller;
//    private DTSController dTSController;

    @Test
    public void AspectTest1(){
//        controller.set(controller);
//        controller.test1("name","sdfasdfsadf","sssssssssssssss");
    }
//@Activity(businessType = "p2p.lending:name")
//public void test1(String name,String id,String pas){
//    dTSController.test2(name,id,"dddddddddd");
//}
//
//    @Action(name = "p2p.lending:name")
//    public void test2(String name,@Instruction String id, String pas){
//
//    }
//    public void set(Object object){
//        this.dTSController = (DTSController) object;
//    }
}
