package com.itiancai.galaxy.dts.demo.server

import com.itiancai.galaxy.dts.action.demo.thrift.DTSActionDemoServerApi
import com.itiancai.galaxy.dts.annotation.{Action, ActionInstruction, Activity, ActivityBusiness}
import com.twitter.util.Future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DTSDemoService {

  private var dtsDemoService:DTSDemoService = null


  @Autowired
  val actionDemoService:DTSActionDemoServerApi.FutureIface = null

  @Activity(businessType = "p2p:activity:activitySearch",isImmediately = false)
  def activityDTSDemo(@ActivityBusiness id: String): Future[String] = {
     dtsDemoService.actionDTSDemo(id)
  }

  @Action(name = "p2p:action:actionDemoDone")
  def actionDTSDemo(@ActionInstruction id: String): Future[String] = {
    actionDemoService.demoAction(id)
  }

  def set(dtsDemoService:DTSDemoService): Unit ={
    this.dtsDemoService = dtsDemoService
  }
}
