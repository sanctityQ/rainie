package com.itiancai.galaxy.dts

import com.itiancai.dts.client.recovery.ActivityStateResolverAImpl
import com.itiancai.galaxy.dts.annotation.{Param, Action, Activity}
import com.twitter.util.Future
import org.springframework.stereotype.Component

import scala.beans.BeanProperty


@Component
class TestScalaController {

  private var controller: TestScalaController = null


  @Activity(businessType = classOf[ActivityStateResolverAImpl])
  def testSuccessActivity(name: String, @Param id: String, pas: String):Future[String] ={
    controller.testSuccessAction(name,id,pas)
    Future("success")
  }
  @Action(name = "p2p:lending:name")
  def testSuccessAction(name: String, @Param id: String, pas: String) {
  }


  @Activity(businessType = classOf[ActivityStateResolverAImpl])
  def testFailActivity(name: String, id: String, pas: String):Future[String] ={
    controller.testFailAction(name, id, "dddddddddd")
    Future("success")
  }


  @Action(name = "p2p:lending:name")
  def testFailAction(name: String, @Param id: String, pas: String) {
  }

  case class TestJson1(@BeanProperty name:String)
  case class TestJson(@BeanProperty name:String,@BeanProperty test1:TestJson1)

  @Activity(businessType = classOf[ActivityStateResolverAImpl])
  def testJsonActivity(name: String, @Param id: String, pas: String):Future[String] ={
    val test1 = new TestJson1("tiancai")
    controller.testJsonAction(new TestJson("tiancaijinrong",test1), id)
    Future("success")
  }

  @Action(name = "p2p:lending:name")
  def testJsonAction(test: Any, @Param id: String) {
  }



  def set(obj: Any) {
    this.controller = obj.asInstanceOf[TestScalaController]
   }
}
