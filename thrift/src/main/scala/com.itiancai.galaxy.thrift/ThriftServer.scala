package com.itiancai.galaxy.thrift

import java.net.InetSocketAddress

import com.itiancai.galaxy.inject.server.{PortUtils, TwitterServer}
import com.itiancai.galaxy.logger.module.Slf4jBridgeModule
import com.twitter.common.zookeeper.{ServerSetImpl, ZooKeeperClient}
import com.twitter.finagle.{Thrift, ListeningServer}
import com.twitter.util.{Future, Time, Await}
import com.twitter.common.quantity.{Time => Time2,Amount}
import grizzled.slf4j.Logging
import com.twitter.conversions.time._
import org.apache.commons.lang.StringUtils
import scala.collection.JavaConverters._




trait ThriftServer extends TwitterServer with Logging{

  addFrameworkModule(
    Slf4jBridgeModule)

  private val thriftPortFlag = flag("thrift.port", ":9999", "External Thrift server port")

  private val thriftShutdownTimeout = flag("thrift.shutdown.time", 1.minute, "Maximum amount of time to wait for pending requests to complete on shutdown")

  private val zkServerFlag = flag("zk.port",StringUtils.EMPTY, "zkServer port, mutilServer use ',' seperator")

  private val zkNodePath = flag("zk.path", "/" + name, "zkServer node path, default is className")

  private val zkServerSessionTimeOut = flag("zk.sessiontimeOut", 1000, "zkServer session timeOut time")

  /* Private Mutable State */
  private var thriftServer: ListeningServer = _

  protected def configureThrift(router: ThriftRouter)

  protected def run(): Unit = {}

  private val amount = Amount.of(zkServerSessionTimeOut(), Time2.MILLISECONDS)

  private[this] def zkServerFlagError: IllegalArgumentException =
    new IllegalArgumentException("zkServerFlag '%s' format error, use 'ip:port,ip:port'".format(zkServerFlag()))

  /* Lifecycle */

  override def postWarmup() {
    super.postWarmup()

    val router = injector.instance[ThriftRouter]
    router.serviceName(name)
    configureThrift(router)
    thriftServer = Thrift.serveIface(thriftPortFlag(), router.filteredService)

    if(zkServerFlag.isDefined){
      val inetSocketAddresses: Seq[InetSocketAddress] =
        for (address <- zkServerFlag().split(",")) yield {
          address.split(":") match {
            case Array(ip,port) => new InetSocketAddress(ip, port.toInt)
            case _ => throw zkServerFlagError
          }
        }
      info("zkServer ip: %s , zkSessionTimeout: %s, zkNodePath:%s".
        format(inetSocketAddresses, amount, zkNodePath()))

      val zkClient = new ZooKeeperClient(amount, inetSocketAddresses.asJava)
      val serverSet = new ServerSetImpl(zkClient, zkNodePath())
      serverSet.join(thriftServer.boundAddress.asInstanceOf[InetSocketAddress], Map[String, InetSocketAddress]().asJava)
    }

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
