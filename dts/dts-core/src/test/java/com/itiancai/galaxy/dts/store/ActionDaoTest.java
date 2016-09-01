package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.IDFactory;
import com.itiancai.galaxy.dts.domain.Status;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = true, transactionManager = "dtsTransactionManager")
public class ActionDaoTest extends AbstractTransactionalJUnit4SpringContextTests {


  @Autowired
  private ActionDao actionDao;

  @Test
  public void testFindByTxId() {
    List<Action> actionList = actionDao.listByTxId("BusinessId40");
    System.out.println(actionList);
  }

  @Test
  @Transactional
  public void testSave() {
    Action action1 = new Action();
    action1.setTxId("txId1");
    action1.setActionId(new IDFactory().getActionId("p2p:lending:bindInviteCode"));
    action1.setServiceName("p2p:lending:bindInviteCode");
    action1.setStatus(Status.Action.PREPARE.getStatus());
    action1.setInstructionId(UUID.randomUUID().toString());
    actionDao.save(action1);
  }

  @Test
  public void testfindByActionId() {
    Action action1 = new Action();
    action1.setTxId("txId1");
    String actionId = new IDFactory().getActionId("p2p:lending:bindInviteCode");
    action1.setActionId(actionId);
    action1.setServiceName("p2p:lending:bindInviteCode");
    action1.setStatus(Status.Action.PREPARE.getStatus());
    action1.setInstructionId(UUID.randomUUID().toString());
    actionDao.save(action1);
    Action action = actionDao.findByActionId(actionId);
    Assert.assertTrue(action != null);

  }

  @Test
  @Transactional
  public void testUpdateStatusByIdStatus() {
    Action action1 = new Action();
    action1.setTxId("txId1");
    String actionId = new IDFactory().getActionId("p2p:lending:bindInviteCode");
    action1.setActionId(actionId);
    action1.setServiceName("p2p:lending:bindInviteCode");
    action1.setStatus(Status.Action.UNKNOWN.getStatus());
    action1.setInstructionId(UUID.randomUUID().toString());
    actionDao.save(action1);
    int num = actionDao.updateStatusByIdStatus(actionId,Status.Action.PREPARE.getStatus(),Status.Action.UNKNOWN.getStatus());
    Assert.assertTrue(num > 0);
  }


}
