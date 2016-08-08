package com.itiancai.galaxy.dts

import org.springframework.stereotype.Component

import scala.collection.mutable


@Component
class NameResolver {
  private val map = mutable.Map[String, String]()
  /**
    * 检查name在服务端是否存在对应的bean
    *
    * @param name 服务名 服务名.模块名:服务名
    * @throws Exception 调用path name不存在或请求异常
    */
  def checkActionName(name: String): Unit = {
    if (!map.contains(getName(name))) {
      getRemotePath(name)

    }
  }

  /**
    * 获取地址服务调用地址
    *
    * @param name
    * @return
    */
  def getPath(name: String): Option[String] = {
    checkActionName(name)
    return map.get(getName(name))
  }


  /**
    * TODO 在server中获取path并缓存
    *
    * @param name
    */
  private def getRemotePath(name: String): Unit = {
    val names: String = getName(name)
    //    //TODO CLIENT
    //    client(request).map(response => {
    //      val path = "DD";
    //      map.put(names[0],path)
    //    }).handle({
    //      throw new DTSException("DTSService is error!!" )
    //    })
  }



  /**
    * 解析字符串并检验格式是否正确
    *
    * @param name
    * @return
    */
  def getName(name: String): String = {
    name.substring(0,name.lastIndexOf(":"))
  }
}
