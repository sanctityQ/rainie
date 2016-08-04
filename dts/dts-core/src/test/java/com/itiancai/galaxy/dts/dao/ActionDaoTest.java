package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.DatasourceTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;
import java.util.List;

/**
 * Created by lsp on 16/8/3.
 */
@ContextConfiguration(classes = {DatasourceTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class ActionDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ActionDao actionDao;

    @Test
    public void actionSave(){
        Action action = new Action();
        action.setActionId(IdGenerator.genActionId());
        action.setContext("{id=1}");
        action.setcTime(new Date());
        action.setmTime(new Date());
        action.setTxId(IdGenerator.genTXId());
        action.setInstructionId("1234567891111");
        action.setServiceName("actionService");
        action.setStatus(Status.Action.FAILL);
        actionDao.save(action);
    }
    @Test
    public void listActionByActionId(){
//        Action action = actionDao.findActionByActionId(760730838873669632L);
//        System.out.println("listActionByActionId==="+action.getActionId());
    }
    @Test
    public void listActionByTxId(){
//        List<Action> action = actionDao.findActionByTxId(760727064620826625L);
//        System.out.println("listActionByTxId==="+action.get(0).getActionId());
    }


}
