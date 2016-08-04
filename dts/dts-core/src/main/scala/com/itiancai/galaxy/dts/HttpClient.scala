package com.itiancai.galaxy.dts

import com.twitter.finagle.{Service, Http}
import com.twitter.finagle.http.{Response, Method, Version, Request}
import com.twitter.util.{Future, Duration}


object HttpClient {

  private val map:java.util.Map[String,Service[Request, Response]] = new java.util.HashMap[String,Service[Request, Response]]

  private def getClient(key:String):Service[Request, Response] ={
    if(!map.containsKey(key)){
      val path = "";
      map.put(key,Http.client.withRequestTimeout(Duration.fromMilliseconds(60000)).newService("path"))
    }
    map.get(key)
  }


  def handleRPC(serverUrl:String,map:java.util.Map[String,Object]):Future[Response] ={
    val request = Request(Version.Http11, Method.Get, "/")
    request.headerMap.add("User-Agent", "Finagle 0.0")
    request.headerMap.add("Host", "")
//    if(!map.isEmpty){
//      for(key:String <- map.keySet()){
//        request.params.+= (key -> map.get(key))
//      }
//    }
    getClient("String")(request)
  }

}
