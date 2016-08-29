package com.itiancai.galaxy.dts.store;

import com.itiancai.galaxy.dts.SpringBootTest;
import com.itiancai.galaxy.dts.domain.Action;
import com.itiancai.galaxy.dts.domain.IdGenerator;
import com.itiancai.galaxy.dts.domain.Status;
import com.itiancai.galaxy.dts.store.ActionDao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by bao on 16/8/5.
 */
@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
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
    action1.setActionId(new IdGenerator().getActionId("p2p:lending:bindInviteCode"));
    action1.setServiceName("p2p:lending:bindInviteCode");
    action1.setStatus(Status.Action.PREPARE.getStatus());
    action1.setInstructionId(UUID.randomUUID().toString());
    action1.setContext("context");
    actionDao.save(action1);
  }


}
