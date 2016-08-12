package com.itiancai.galaxy.dts.demo.server

import java.util.concurrent.{TimeUnit, Executors, ScheduledExecutorService}
import javax.annotation.PostConstruct

import com.itiancai.galaxy.dts.action.demo.thrift.DTSActionDemoServerApi
import com.itiancai.galaxy.inject.Logging
import com.itiancai.galaxy.thrift.Controller
import com.twitter.util.Future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import thrift.DTSDemoServerApi
import thrift.DTSDemoServerApi.DemoActivity

@Component
class DemoDTSController extends Controller with DTSDemoServerApi.BaseServiceIface with Logging{

  @Autowired
  private var dtsDemoService: DTSDemoService = null

  var executorService: ScheduledExecutorService = null

  @PostConstruct
  def init(): Unit = {
    executorService = Executors.newScheduledThreadPool(1)
      executorService.schedule(new Runnable {
        override def run(): Unit = {
          while (true) {
            dtsDemoService.set(dtsDemoService)
            dtsDemoService.activityDTSDemo("testDemo")
            Future("testDemo")
            Thread.sleep(10000)
          }
        }
      }, 10, TimeUnit.SECONDS)
  }



  override def demoActivity = handle(DemoActivity)({
    args => {
      dtsDemoService.set(dtsDemoService)
      dtsDemoService.activityDTSDemo(args.id)
      Future(args.id)
    }
  })
}
