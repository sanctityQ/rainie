package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.DatasourceTest;
import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContextConfiguration(classes = {DatasourceTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class ActivityDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ActivityDao activityDao;

    @Test
    public void save(){
        Activity activity = new Activity();
        activity.setBusinessId("123456789lll");
        activity.setBusinessType("p2p.lending:save");
        activity.setcTime(new Date());
        activity.setmTime(new Date());
        activity.setFinish(0);
        activity.setStatus(Status.Activity.FAILL);
        activity.setTimeOut(30000);
        activity.setTxId(IdGenerator.genTXId());

        List<Activity> list = new ArrayList<Activity>();
        list.add(activity);
        activityDao.save(list);
    }

    @Test
    public void findActivityByTxId(){
      Activity activity = activityDao.findActivityByTxId(760735587194896385L);
      System.out.println("findActivityByTxId==" + activity.getBusinessId());
    }
}
