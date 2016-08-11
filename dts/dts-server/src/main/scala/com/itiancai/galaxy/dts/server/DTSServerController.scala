package com.itiancai.galaxy.dts.server

import com.itiancai.galaxy.dts.thrift.DTSServerApi
import com.itiancai.galaxy.dts.thrift.DTSServerApi.ServicePath
import com.itiancai.galaxy.dts.utils.NameResolver
import com.itiancai.galaxy.thrift.Controller
import com.twitter.util.Future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
  * Created by bao on 16/8/10.
  */
@Component
class DTSServerController extends Controller with DTSServerApi.BaseServiceIface {

  @Autowired
  val env:Environment = null

  /**
    * 通过sysName和moduleName获取servicePath
    */
  override val servicePath = handle(ServicePath) ({
    args => {
      info(s"servicePath--sysName:${args.sysName}, moduleName:${args.moduleName}")
      //TODO 异常处理
      val pathKey = NameResolver.pathKey(args.sysName, args.moduleName)
      val path = env.getProperty(pathKey, classOf[String])
      Future(path)
    }
  })
}
