package com.itiancai.galaxy.dts;


import com.itiancai.galaxy.dts.domain.IdGenerator;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(classes = {SpringBootTest.class})
@TransactionConfiguration(defaultRollback = false, transactionManager = "dtsTransactionManager")
public class IdConfigureTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    IdGenerator idGenerator;

    @Test
    public void testActionId(){
        Assert.assertTrue(idGenerator.getActionId("lsp").contains("lsp"));
    }
    @Test
    public void testTxId(){
        Assert.assertTrue(idGenerator.getTxId("lsp").contains("lsp"));
    }
}
