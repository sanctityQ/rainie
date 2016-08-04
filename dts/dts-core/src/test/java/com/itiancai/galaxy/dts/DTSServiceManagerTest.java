package com.itiancai.galaxy.dts;

import com.itiancai.galaxy.dts.domain.Status;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;


@ContextConfiguration(classes = {DatasourceTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class DTSServiceManagerTest  extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private DTSServiceManager manager;

    @Test
    public void startActivity(){
        manager.startActivity("987654321","p2p.lending:invest",6000);
    }
    @Test
    public void finishActivity(){
        manager.finishActivity(Status.Activity.FAILL,false);
    }
    @Test
    public void startAction(){
        manager.startAction("0987654321","p2p.lending:invest","{name=p2p.lending:invest}");
    }
    @Test
    public void finishAction(){
       manager.finishAction(Status.Action.PREPARE,760730838873669632l);
    }
}
