package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class ActivityDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private IdGenerator idGenerator;
    @Test
    public void save(){
        Activity activity = new Activity();
        activity.setBusinessId("123456789lll");
        activity.setBusinessType("p2p.lending:::save");
        activity.setcTime(new Date());
        activity.setmTime(new Date());
        activity.setFinish(0);
        activity.setStatus(Status.Activity.FAIL);
        activity.setTimeOut(30000);
        activity.setTxId(idGenerator.getTxId("p2p.lending:::save"));

        List<Activity> list = new ArrayList<Activity>();
        list.add(activity);
        Assert.assertTrue(activityDao.save(list) instanceof List);
    }
    @Test
    public void findByTxId(){
        Assert.assertTrue(activityDao.findByTxId("tx:p2p.lending:::save:343ed264a0e717") instanceof Activity);
    }
    @Test
    public void updateActivityFinish(){
        Assert.assertTrue(activityDao.updateActivityFinish("tx:p2p.lending:::save:343ed264a0e717",3) == 1);
    }
    @Test
    public void updateAcvityStatus(){
        Assert.assertTrue(activityDao.updateAcvityStatus("TX:p2p.lending:name:343ed0a051d673",2,2) == 1);
    }
}
