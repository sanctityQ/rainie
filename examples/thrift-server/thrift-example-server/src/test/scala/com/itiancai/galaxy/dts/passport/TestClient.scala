package com.itiancai.galaxy.dts.passport

import com.itiancai.passport.dao.ExecuteDao
import com.itiancai.passport.thrift.Source.Web
import com.itiancai.passport.thrift._
import com.twitter.finagle.{Service, SimpleFilter, Thrift}
import com.twitter.util.Future._
import com.twitter.util.{Timer, Duration, Await, Future}
import org.apache.commons.lang.{ObjectUtils, ClassUtils}
import org.junit.runner.RunWith
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class TestClient extends WordSpec{

  "be allowed" in {

//    val amount = Amount.of(1000, Time.MILLISECONDS)
//    val inetSocketAddress = new InetSocketAddress("192.168.0.11", 2181)
//    val zkClient = new ZooKeeperClient(amount, inetSocketAddress)
//    val zk: org.apache.zookeeper.ZooKeeper = zkClient.get
//    val jsonCodec = ServerSetImpl.createJsonCodec
//    val serverInstances: Seq[ServiceInstance] =
//      for (zNode <- zk.getChildren("/finagle", false) ) yield
//    {
//
//        val serverData = zk.getData(s"/finagle/$zNode", false, null)
//        //serverData is Array[Byte] of JSON
//        //val json = new String(serverData, "UTF-8")
//        ServerSets.deserializeServiceInstance(serverData, jsonCodec)
//    }






    //client 1
//    val client = Thrift.newIface[PassportService[Future]]("127.0.0.1:8000")//123.57.227.107
    val client = Thrift.newIface[PassportService.FutureIface]("zk2!192.168.0.173:2182!/testServer")//123.57.227.107

//    val userLogin = UserLogin.apply("aaa","1123123",Web)


    val result1: Future[RegResponse] = client.reg(RegRequest("13333333333"))
//
//
    println("aaa" + Await.result(result1))
//
//
//    val result2: Future[Boolean] = client.userInfo()
//    println("bbb" + Await.result(result2))
//
//    var i = 0
//    while (i<100000) {
//      client.login(userLogin).onSuccess{
//        response => println(s"aaa${i}" + response.errMsg)
//          i += 1
//      }.onFailure{
//        exception=>println("a3"+exception)
//          i += 1
//      }
//      println(s"aaa${i}" + Await.result(result1, DEFAULT_TIMEOUT).errMsg)
//      i += 1
//    }



//    val clientServiceIface: PassportService.ServiceIface = Thrift.newServiceIface[PassportService.ServiceIface]("192.168.0.11:8000")
//    val clientServiceIface: PassportService.ServiceIface = Thrift.newServiceIface[PassportService.ServiceIface]("zk2!192.168.0.11:2182!/com.itiancai.passport.thrift.TestServer")


//    val result2: Future[Login.Result] = clientServiceIface.login(Login.Args(userLogin))
    //#thriftclientapi-call

//    println("aaa3" + Await.result(result2).success.get.errMsg)


    //
//    val uppercaseFilter = new SimpleFilter[Login.Args, Login.Result] {
//      def apply(req: Login.Args, service: Service[Login.Args, Login.Result]): Future[Login.Result] = {
//        //val uppercaseRequest = req.copy(message = req.toUpperCase)
//        service(req)
//      }
//    }
//    val mdcFilter = new LoggingMDCFilter[Login.Args,Login.Result]
//    val service = mdcFilter andThen uppercaseFilter andThen clientServiceIface.login
    //  //uppercaseFilter andThen clientServiceIface.login
    //new ExpiringService(service, Option(Duration.fromSeconds(1)), Option)
  }

}
