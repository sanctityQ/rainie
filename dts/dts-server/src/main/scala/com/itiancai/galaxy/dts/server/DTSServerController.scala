package com.itiancai.galaxy.dts.server

import com.itiancai.galaxy.dts.server.RecoveryService.ServicePath
import com.itiancai.galaxy.thrift.Controller
import com.twitter.util.Future
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class RecoveryController extends Controller with RecoveryService.BaseServiceIface {

  @Autowired
  val env:Environment = null

  /**
    * 通过sysName和moduleName获取servicePath
    */
  override val servicePath = handle(ServicePath) ({
    args => {
      info(s"servicePath--sysName:${args.sysName}, moduleName:${args.moduleName}")
      val pathKey = s"recovery.${args.sysName}.${args.moduleName}"
      if(StringUtils.isBlank(pathKey)) throw new ServiceNotFindException
      val path = env.getProperty(pathKey)
      Future(path)
    }
  })
}
