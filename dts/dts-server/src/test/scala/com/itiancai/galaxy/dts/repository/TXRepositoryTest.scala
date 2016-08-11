package com.itiancai.galaxy.dts.repository

import com.itiancai.galaxy.dts.dao.{ActionDao, ActivityDao}
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}

/**
  * Created by bao on 16/8/9.
  */
class TXRepositoryTest extends WordSpec with BeforeAndAfterEach {

  val activityDaoM = mock(classOf[ActivityDao])

  val actionDaoM = mock(classOf[ActionDao])

  val txRepository = new TXRepository {
    override val activityDao = activityDaoM
    override val actionDao = actionDaoM
  }

  override protected def beforeEach(): Unit = {
    reset(activityDaoM, actionDaoM)
  }

}
