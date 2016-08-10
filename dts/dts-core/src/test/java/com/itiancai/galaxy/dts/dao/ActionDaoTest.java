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

import java.util.List;
import java.util.UUID;

/**
 * Created by bao on 16/8/5.
 */
@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class ActionDaoTest extends AbstractJUnit4SpringContextTests {


  @Autowired
  private ActionDao actionDao;

  @Test
  public void testFindByTxId() {
    List<Action> actionList = actionDao.findByTxId("761438762969333760");
    System.out.println(actionList);
  }


}
