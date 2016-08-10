package com.itiancai.galaxy.dts.dao;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.UUID;

/**
 * Created by bao on 16/8/5.
 */
@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class ActivityDaoTest extends AbstractJUnit4SpringContextTests {


  @Autowired
  private ActivityDao activityDao;

  @Autowired
  private ActionDao actionDao;

  @Test
  public void testFindByTxId() {
    Activity activity = activityDao.findByTxId("761438762969333760");
    System.out.println(activity);
  }

  /**
   * 初始化测试数据
   */
  @Test
  public void initTestData() {
    for(int i=0; i<100000; i++) {
      Activity activity = new Activity();
      activity.setBusinessId("BusinessId"+i);
      activity.setBusinessType("p2p.interact:triggerInteract");
      activity.setCollect(0);
      activity.setStatus(Status.Activity.SUCCESS);
      String txId = new IdGenerator().getTxId("p2p.interact:triggerInteract");
      activity.setTxId(txId+"");
      activity.setTimeOut(1000);

      Action action1 = new Action();
      action1.setTxId(txId+"");
      action1.setActionId(new IdGenerator().getActionId("p2p.lending:bindInviteCode"));
      action1.setServiceName("p2p.lending:bindInviteCode");
      action1.setStatus(Status.Action.PREPARE);
      action1.setInstructionId(UUID.randomUUID().toString());
      action1.setContext("context");

      Action action2 = new Action();
      action2.setTxId(txId+"");
      action2.setActionId(new IdGenerator().getActionId("p2p.user:bindInviteCode"));
      action2.setServiceName("p2p.user:bindInviteCode");
      action2.setStatus(Status.Action.PREPARE);
      action2.setInstructionId(UUID.randomUUID().toString());
      action2.setContext("context");

      activityDao.save(activity);
      actionDao.save(action1);
      actionDao.save(action2);
    }
  }
}
