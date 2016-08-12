package com.itiancai.galaxy.dts.action.demo.server

import com.itiancai.galaxy.dts.action.demo.thrift.DTSActionDemoServerApi
import com.itiancai.galaxy.dts.action.demo.thrift.DTSActionDemoServerApi.DemoAction
import com.itiancai.galaxy.inject.Logging
import com.itiancai.galaxy.thrift.Controller
import com.twitter.util.Future
import org.springframework.stereotype.Component

@Component
class DemoDTSActionController extends Controller with DTSActionDemoServerApi.BaseServiceIface with Logging{

  override def demoAction = handle(DemoAction)({
    args => {
      Future(args.id)
    }
  })
}
