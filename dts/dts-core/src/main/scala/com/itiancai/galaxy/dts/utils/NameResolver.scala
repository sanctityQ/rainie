package com.itiancai.galaxy.dts.utils

/**
  * Created by bao on 16/8/11.
  */
object NameResolver {

  val ACTIVITY_HANDLE_PATH = "/dts/activity"

  val ACTION_HANDLE_PATH = "/dts/action"

  def eval(name: String): (String, String, String) = {
    val array = name.split(":")
    if(array.length != 3) {
      //TODO 异常处理
      throw new RuntimeException(s"resolve name:[${name}] error")
    }
    (array(0), array(1), array(2))
  }

  def pathKey(sysName: String, moduleName: String):String = {
    s"recovery.${sysName}.${moduleName}"
  }

}
