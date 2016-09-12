package com.itiancai.galaxy.dts.demo.server

import com.itiancai.galaxy.dts.action.demo.thrift.DTSActionDemoServerApi
import com.twitter.finagle.Thrift
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

/**
  * Created by bao on 16/1/12.
  */
@Component
class ServiceFactory {

  @Value("${dts.action.demo}")
  private[this] val userUrl:String = null

  @Bean
  def actionDemoService(): DTSActionDemoServerApi.FutureIface = {
    Thrift.newIface[DTSActionDemoServerApi.FutureIface](userUrl)
  }
}
