package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.Activity;
import com.itiancai.galaxy.dts.domain.Status;
import com.itiancai.galaxy.dts.support.ServiceName;
import com.itiancai.galaxy.dts.support.XidFactory;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = true, transactionManager = "dtsTransactionManager")
public class ActivityDaoTest extends AbstractJUnit4SpringContextTests {


  @Autowired
  private ActivityDao activityDao;

  @Autowired
  private ActionDao actionDao;

  @Test
  public void testFindByTxId() {
    Activity activity = new Activity();
    activity.setBusinessId("BusinessId");
    activity.setBusinessType("p2p:interact:triggerInteract");
    activity.setStatus(Status.Activity.UNKNOWN.getStatus());
    String txId = XidFactory.newXid(ServiceName.apply("p2p:interact:triggerInteract")).getGlobalTransactionId();
    activity.setTxId(txId+"");
    activityDao.save(activity);

    Activity activity1 = activityDao.findByTxId(txId);
    Assert.assertTrue(activity1 != null);
  }

  @Test
  public void testUpdateStatusByTxIdStatus(){
    Activity activity = new Activity();
    activity.setBusinessId("BusinessId");
    activity.setBusinessType("p2p:interact:triggerInteract");
    activity.setStatus(Status.Activity.UNKNOWN.getStatus());
    String txId = XidFactory.newXid(ServiceName.apply("p2p:interact:triggerInteract")).getGlobalTransactionId();
    activity.setTxId(txId+"");
    activityDao.save(activity);

    int num = activityDao.updateStatusByTxIdStatus(txId,Status.Activity.SUCCESS.getStatus(),Status.Activity.UNKNOWN.getStatus());
    Assert.assertTrue(num > 0);

  }

  @Test
  public void testFinishActivityAndlockTXByTxIdAndStatus(){
    Activity activity = new Activity();
    activity.setBusinessId("BusinessId");
    activity.setBusinessType("p2p:interact:triggerInteract");
    activity.setStatus(Status.Activity.SUCCESS.getStatus());
    String txId = XidFactory.newXid(ServiceName.apply("p2p:interact:triggerInteract")).getGlobalTransactionId();
    activity.setTxId(txId+"");
    activityDao.save(activity);
    activityDao.lockTXByTxIdAndStatus(txId,Status.Activity.SUCCESS,10);
    int num = activityDao.finishActivity(txId);
    Assert.assertTrue(num == 1);
  }

  @Test
  public void testListUnfinished(){
    Activity activity = new Activity();
    activity.setBusinessId("BusinessId");
    activity.setBusinessType("p2p:interact:triggerInteract");
    activity.setStatus(Status.Activity.UNKNOWN.getStatus());
    String txId = XidFactory.newXid(ServiceName.apply("p2p:interact:triggerInteract")).getGlobalTransactionId();
    activity.setTxId(txId+"");
    activityDao.save(activity);
    List<String> list = activityDao.listUnfinished(1,1,3000);
    Assert.assertTrue(list.isEmpty());
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
      String txId = XidFactory.newXid(ServiceName.apply("p2p:interact:triggerInteract")).getGlobalTransactionId();
      activity.setTxId(txId+"");

      Action action1 = new Action();
      action1.setTxId(txId+"");
      action1.setActionId(XidFactory.newXid(ServiceName.apply("p2p:user:bindInviteCode")).getGlobalTransactionId());
      action1.setServiceName("p2p:lending:bindInviteCode");
      action1.setStatus(Status.Action.PREPARE.getStatus());
      action1.setInstructionId(UUID.randomUUID().toString());

      Action action2 = new Action();
      action2.setTxId(txId+"");
      action2.setActionId(XidFactory.newXid(ServiceName.apply("p2p:user:bindInviteCode")).getGlobalTransactionId());
      action2.setServiceName("p2p:user:bindInviteCode");
      action2.setStatus(Status.Action.PREPARE.getStatus());
      action2.setInstructionId(UUID.randomUUID().toString());

      activityDao.save(activity);
      actionDao.save(action1);
      actionDao.save(action2);
    }
  }
}
