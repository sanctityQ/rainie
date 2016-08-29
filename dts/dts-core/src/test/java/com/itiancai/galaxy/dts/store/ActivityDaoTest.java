package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;
import java.util.UUID;

/**
 * Created by bao on 16/8/5.
 */
@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class ActivityDaoTest extends AbstractTransactionalJUnit4SpringContextTests {


  @Autowired
  private ActivityDao activityDao;

  @Autowired
  private ActionDao actionDao;

  @Test
  public void testFindByTxId() {
    Activity activity = activityDao.findByTxId("tx:p2p:interact:triggerInteract:156cfe83f6a677b88ea");
    System.out.println(activity);
  }

  @Test
  public void testSave() {
    Activity activity = new Activity();
    activity.setBusinessId("BusinessIdxxx2");
    activity.setBusinessType("p2p:interact:triggerInteract");
    activity.setStatus(Status.Activity.SUCCESS.getStatus());
    String txId = new IdGenerator().getTxId("p2p:interact:triggerInteract");
    activity.setTxId(txId + "");
    activity.setTimeOut(1000);
    activityDao.save(activity);
  }

  @Test
  public void testListSuccessOrFail() {
    List<String> list = activityDao.listSuccessOrFail(1, 8, 10);
    System.out.println(list);
  }

  /**
   * 初始化测试数据
   */
  @Test
  public void initTestData() {
    for(int i=0; i<100; i++) {
      Activity activity = new Activity();
      activity.setBusinessId("BusinessId"+i);
      activity.setBusinessType("p2p:interact:triggerInteract");
      activity.setStatus(Status.Activity.SUCCESS.getStatus());
      String txId = new IdGenerator().getTxId("p2p:interact:triggerInteract");
      activity.setTxId(txId+"");
      activity.setTimeOut(1000);

      Action action1 = new Action();
      action1.setTxId(txId+"");
      action1.setActionId(new IdGenerator().getActionId("p2p:lending:bindInviteCode"));
      action1.setServiceName("p2p:lending:bindInviteCode");
      action1.setStatus(Status.Action.PREPARE.getStatus());
      action1.setInstructionId(UUID.randomUUID().toString());
      action1.setContext("context");

      Action action2 = new Action();
      action2.setTxId(txId+"");
      action2.setActionId(new IdGenerator().getActionId("p2p:user:bindInviteCode"));
      action2.setServiceName("p2p:user:bindInviteCode");
      action2.setStatus(Status.Action.PREPARE.getStatus());
      action2.setInstructionId(UUID.randomUUID().toString());
      action2.setContext("context");

      activityDao.save(activity);
      actionDao.save(action1);
      actionDao.save(action2);
    }
  }
}
