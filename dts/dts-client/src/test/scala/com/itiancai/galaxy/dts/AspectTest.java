package com.itiancai.galaxy.dts;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;


@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class AspectTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private TestScalaController testScalaController;
    @Autowired
    private TestJavaController testJavaController;

    @Test
    public void successScalaFuture(){
        testScalaController.set(testScalaController);
        String str =  testScalaController.testSuccessActivity("name","1111111111","sssssssssssssss").toString();
        Assert.assertEquals(str,"success");
    }

    @Test
    public void failScalaFuture(){
        testScalaController.set(testScalaController);
        try{
           testScalaController.testFailActivity("name","1111111111","sssssssssssssss");
        }catch(Exception e){
            Assert.assertTrue(e instanceof DTSException);
        }
    }


    @Test
    public void jsonScalaFuture(){
        testScalaController.set(testScalaController);
        Assert.assertEquals(testScalaController.testJsonActivity("name","1111111111","sssssssssssssss"),"success");;
    }

    @Test
    public void successJavaReturn(){
        testJavaController.set(testJavaController);
        Assert.assertEquals(testJavaController.testSuccessActivity("name","1111111111","sssssssssssssss"),"success");
    }

    @Test
    public void failJavaReturn(){
        testJavaController.set(testJavaController);
        try{
            testJavaController.testFailActivity("name","1111111111","sssssssssssssss");
        }catch(Exception e){
            Assert.assertTrue(e instanceof DTSException);
        }
    }

    @Test
    public void jsonJavaReturn(){
        testJavaController.set(testJavaController);
        Assert.assertEquals(testJavaController.testSuccessJsonAcvity("name","1111111111","sssssssssssssss"),"success");;
    }

    @Test
    public void failActionJavaReturn(){
        testJavaController.set(testJavaController);
        try {
            testJavaController.testFailAcActivity("name","1111111111","sssssssssssssss");
        }catch (Exception e){
            Assert.assertTrue(e instanceof DTSException);
        }
    }

}
