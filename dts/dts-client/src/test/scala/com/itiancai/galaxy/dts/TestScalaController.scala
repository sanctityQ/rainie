package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.annotation.{ActionInstruction, ActivityBusiness, Action, Activity}
import com.twitter.util.Future
import org.springframework.stereotype.Component

import scala.beans.BeanProperty


@Component
class TestScalaController {

  private var controller: TestScalaController = null


  @Activity(businessType = "p2p:lending:name")
  def testSuccessActivity(name: String, @ActivityBusiness id: String, pas: String):Future[String] ={
    controller.testSuccessAction(name,id,pas)
    Future("success")
  }
  @Action(name = "p2p:lending:name")
  def testSuccessAction(name: String, @ActionInstruction id: String, pas: String) {
  }


  @Activity(businessType = "p2p:lending:name")
  def testFailActivity(name: String, id: String, pas: String):Future[String] ={
    controller.testFailAction(name, id, "dddddddddd")
    Future("success")
  }


  @Action(name = "p2p:lending:name")
  def testFailAction(name: String, @ActionInstruction id: String, pas: String) {
  }

  case class TestJson1(@BeanProperty name:String)
  case class TestJson(@BeanProperty name:String,@BeanProperty test1:TestJson1)

  @Activity(businessType = "p2p:lending:name")
  def testJsonActivity(name: String, @ActivityBusiness id: String, pas: String):Future[String] ={
    val test1 = new TestJson1("tiancai")
    controller.testJsonAction(new TestJson("tiancaijinrong",test1), id)
    Future("success")
  }

  @Action(name = "p2p:lending:name")
  def testJsonAction(test: Any, @ActionInstruction id: String) {
  }



  def set(obj: Any) {
    this.controller = obj.asInstanceOf[TestScalaController]
   }
}
