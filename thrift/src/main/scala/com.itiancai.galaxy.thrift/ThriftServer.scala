package com.itiancai.galaxy.thrift

import com.itiancai.galaxy.inject.server.{PortUtils, TwitterServer}
import com.itiancai.galaxy.logger.module.Slf4jBridgeModule
import com.twitter.finagle.{ThriftMux,ListeningServer}
import com.twitter.util.{Future, Time, Await}
import com.twitter.common.quantity.{Time => Time2,Amount}
import grizzled.slf4j.Logging
import com.twitter.conversions.time._
import org.apache.commons.lang.StringUtils


trait ThriftServer extends TwitterServer with Logging{

  addFrameworkModule(
    Slf4jBridgeModule)

  protected def defaultFinatraThriftPort: String = ":9999"
  private val thriftPortFlag = flag("thrift.port", defaultFinatraThriftPort, "External Thrift server port")

  private val thriftShutdownTimeout = flag("thrift.shutdown.time", 1.minute, "Maximum amount of time to wait for pending requests to complete on shutdown")

  protected def defaultThriftServerName: String = "thrift-rainie"

  private val thriftServerNameFlag = flag("thrift.name", defaultThriftServerName, "Thrift server name")

  private val zkServerAddress = flag("zk.address",StringUtils.EMPTY, "zkServer address, mutilServer use ',' seperator")

  private val zkNodePath = flag("zk.path", "/" + thriftServerNameFlag(), "zkServer node path, default is className")

  private val zkAnnounce = "zk"

  /* shareId set default value 0 */
  private val zkShareId: String = "0"


  private val zkServerSessionTimeOut = flag("zk.sessionTimeout", 1000, "zkServer session timeOut time")

  /* Private Mutable State */
  private var thriftServer: ListeningServer = _

  protected def configureThrift(router: ThriftRouter)

  protected def run(): Unit = {}

  private val amount = Amount.of(zkServerSessionTimeOut(), Time2.MILLISECONDS)

  private[this] def zkServerFlagError: IllegalArgumentException =
    new IllegalArgumentException("zkServerFlag '%s' format error, use 'ip:port,ip:port'".format(zkServerAddress()))


  /* Lifecycle */

  override def postWarmup() {
    super.postWarmup()

    val router = injector.instance[ThriftRouter]
    router.serviceName(name)
    configureThrift(router)
    thriftServer = ThriftMux.server.withLabel(defaultThriftServerName)
        .serveIface(thriftPortFlag(), router.filteredService)

    val address = "%s!%s!%s!0".format(zkAnnounce, zkServerAddress(), zkNodePath())

    info("Thrift server register address:" + address)
    // zk!host!/full/path!shardId
    thriftServer.announce(address)

    onExit {
      Await.result(close(thriftServer, thriftShutdownTimeout().fromNow))
    }

    info("Thrift server started on port: " + thriftPort.get)
  }

  override def thriftPort = Option(thriftServer) map PortUtils.getPort

  override final def appMain(): Unit = { run() }


  /* Private */

  private def close(server: ListeningServer, deadline: Time) = {
    if (server != null)
      server.close(deadline)
    else
      Future.Unit
  }

}
