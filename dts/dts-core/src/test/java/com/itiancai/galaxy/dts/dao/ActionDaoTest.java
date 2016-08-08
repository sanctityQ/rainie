package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class ActionDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ActionDao actionDao;
    @Autowired
    private IdGenerator idGenerator;


    @Test
    public void save(){
        Action action = new Action();
        action.setActionId(idGenerator.getActionId("actionService"));
        action.setContext("{id=1}");
        action.setcTime(new Date());
        action.setmTime(new Date());
        action.setTxId(idGenerator.getTxId("actionService"));
        action.setInstructionId("1234567891111");
        action.setServiceName("actionService");
        action.setStatus(Status.Action.FAIL);
        Assert.assertTrue(actionDao.save(action) instanceof Action);
    }
    @Test
    public void findByActionId(){
        Assert.assertTrue(actionDao.findByActionId("ac:p2p.lending:::name:343ee4697a7514").getActionId().equals("ac:p2p.lending:::name:343ee4697a7514") );
    }
    @Test
    public void findByTxId(){
        Assert.assertTrue(actionDao.findByTxId("TX:p2p.lending:name:343ed0a051d673") instanceof List);
    }
    @Test
    public void updateActionStatus(){
        Assert.assertTrue(actionDao.updateActionStatus("AC:p2p.lending:name:343ed0a30f8609",2,2) == 1);

    }



}
