package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.ServerBootTest
import org.junit.Test
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration

/**
  * Created by bao on 16/8/5.
  */
@ContextConfiguration(classes = Array(classOf[ServerBootTest]))
@TransactionConfiguration(defaultRollback = true, transactionManager = "dtsTransactionManager")
class TXRepositorySpringTest extends AbstractJUnit4SpringContextTests {

}
