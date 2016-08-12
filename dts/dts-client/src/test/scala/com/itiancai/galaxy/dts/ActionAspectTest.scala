package com.itiancai.galaxy.dts

import java.lang.reflect.Method

import com.itiancai.galaxy.dts.annotation.{Action, ActionInstruction, Activity, ActivityBusiness}
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, WordSpec}


class ActionAspectTest  extends WordSpec with BeforeAndAfterEach {
  val dtsControllerM = mock(classOf[DTSController])

  val actionAspect = new ActionAspect {
    val dtsController = dtsControllerM
  }
  override protected def beforeEach(): Unit = {
    reset(dtsControllerM)
  }
  //正常
  @Action(name = "p2p:lending:name")
  def actionAspectMethod(@ActionInstruction id: String){}

  //没有参数
  @Action(name = "p2p:lending:name")
  def actionAspectMethodNoParam(){}

  //没有参数注解
  @Action(name = "p2p:lending:name")
  def actionAspectMethodNoInstruction(id: String){}

  //两个参数注解
  @Action(name = "p2p:lending:name")
  def actionAspectMethodTwoInstruction(@ActionInstruction id: String,@ActionInstruction pas: String){}

  //两个参数注解ActivityBusiness
  @Action(name = "p2p:lending:name")
  def actionAspectMethodInstructionAndActivity(@ActionInstruction id: String,@ActivityBusiness pas: String){}

  //name不存在
  @Action(name = "")
  def actionAspectMethodNoName(@ActionInstruction id: String){}


  "actionAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = Map('index'->'0','value'->'')" in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethod",classOf[String])
        val dtsAnName:String = classOf[Action].getName
        val map:Map[String,String] = actionAspect.getParamterAnnotationValue(method,dtsAnName)
        assert(!map.isEmpty)
      }
    }
  }

  "actionAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('Parameters annotation is empty,methodName=actionAspectMethodNoParam')" in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethodNoParam")
        val dtsAnName:String = classOf[Action].getName
        val caught = intercept[DTSException]{
          actionAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }


  "actionAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('The method must specify business parameters or idempotent,methodName=actionAspectMethodNoInstruction')" in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethodNoInstruction",classOf[String])
        val dtsAnName:String = classOf[Action].getName
        val caught = intercept[DTSException]{
          actionAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }

  "actionAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('The method must specify business parameters or idempotent,methodName=actionAspectMethodTwoInstruction')" in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethodTwoInstruction",classOf[String],classOf[String])
        val dtsAnName:String = classOf[Action].getName
        val caught = intercept[DTSException]{
          actionAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }

  "actionAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result =  Map('index'->'0','value'->'')" in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethodInstructionAndActivity",classOf[String],classOf[String])
        val dtsAnName:String = classOf[Action].getName
        val map = actionAspect.getParamterAnnotationValue(method,dtsAnName)
        assert(!map.isEmpty)
      }
    }
  }
  /**
    * getParamterAnnotationValue end
    */

  "actionAspect-getMethodAnnotationValue" when {
    "success return->ActionAnnotation(name,index,value)" should{
      "result = ActionAnnotation('p2p:lending:name',1,'') " in {
        val  test :ActionAspectTest = new ActionAspectTest
        val method: Method = test.getClass.getMethod("actionAspectMethodNoName",classOf[String])
        val dtsAnName:String = classOf[Action].getName
        val map = Map[String,String]("index" -> "1","value" -> "")
        val caught = intercept[DTSException]{
          actionAspect.getMethodAnnotationValue(method,dtsAnName,map)        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }
}
class ActivityAspectTest extends WordSpec with BeforeAndAfterEach {
  val dtsControllerM = mock(classOf[DTSController])

  val activityAspect = new ActivityAspect {
    val dtsController = dtsControllerM
  }
  override protected def beforeEach(): Unit = {
    reset(dtsControllerM)
  }
  //正常
  @Activity(businessType = "p2p:lending:name")
  def activityAspectMethod(@ActivityBusiness id: String){}

  //没有参数
  @Activity(businessType = "p2p:lending:name")
  def activityAspectMethodNoParam(){}

  //没有参数注解
  @Activity(businessType = "p2p:lending:name")
  def activityAspectMethodNoInstruction(id: String){}

  //两个参数注解
  @Activity(businessType = "p2p:lending:name")
  def activityAspectMethodTwoInstruction(@ActivityBusiness id: String,@ActivityBusiness pas: String){}

  //两个参数注解ActivityBusiness
  @Activity(businessType = "p2p:lending:name")
  def activityAspectMethodInstructionAndActivity(@ActionInstruction id: String,@ActivityBusiness pas: String){}

  //name不存在
  @Activity(businessType = "")
  def activityAspectMethodNoName(@ActivityBusiness id: String){}



  "activityAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = Map('index'->'0','value'->'')" in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethod",classOf[String])
        val dtsAnName:String = classOf[Activity].getName
        val map:Map[String,String] = activityAspect.getParamterAnnotationValue(method,dtsAnName)
        assert(!map.isEmpty)
      }
    }
  }

  "activityAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('Parameters annotation is empty,methodName=activityAspectMethodNoParam')" in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethodNoParam")
        val dtsAnName:String = classOf[Activity].getName
        val caught = intercept[DTSException]{
          activityAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }
  "activityAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('The method must specify business parameters or idempotent,methodName=activityAspectMethodNoInstruction')" in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethodNoInstruction",classOf[String])
        val dtsAnName:String = classOf[Activity].getName
        val caught = intercept[DTSException]{
          activityAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }
  "activityAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result = DTSException('activity parameters @interface Instruction there is only one time,methodName=activityAspectMethodNoInstruction')" in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethodTwoInstruction",classOf[String],classOf[String])
        val dtsAnName:String = classOf[Activity].getName
        val caught = intercept[DTSException]{
          activityAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }

  "activityAspect-getParamterAnnotationValue" when {
    "success return->Map[String,String]" should{
      "result =  Map('index'->'0','value'->'')" in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethodTwoInstruction",classOf[String],classOf[String])
        val dtsAnName:String = classOf[Activity].getName
        val caught = intercept[DTSException]{
          activityAspect.getParamterAnnotationValue(method,dtsAnName)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }

  "activityAspect-getMethodAnnotationValue" when {
    "success return->ActivityAnnotation(name,index,value)" should{
      "result = ActionAnnotation('p2p:lending:name',1,'') " in {
        val  test :ActivityAspectTest = new ActivityAspectTest
        val method: Method = test.getClass.getMethod("activityAspectMethodNoName",classOf[String])
        val dtsAnName:String = classOf[Activity].getName
        val map = Map[String,String]("index" -> "1","value" -> "")
        val caught = intercept[DTSException]{
          activityAspect.getMethodAnnotationValue(method,dtsAnName,map)
        }
        assert(caught.isInstanceOf[DTSException])
      }
    }
  }

}