package com.itiancai.galaxy.dts.server

import com.itiancai.galaxy.dts.utils.{ClientFactory, NameResolver}
import com.twitter.finagle.Http
import com.twitter.conversions.time._
import com.twitter.finagle.http.{Method, Request, Version}

/**
  * Created by bao on 16/8/5.
  */
object HttpClientTest extends App {

  val client = Http.client.withRequestTimeout(42.seconds).newService(":8080")
  val request = Request(Version.Http11, Method.Get, NameResolver.ACTION_HANDLE_PATH)
//  request.headerMap.add("User-Agent", "Finagle 0.0")
//  request.headerMap.add("Host", "127.0.0.1")


  client(request).onSuccess(r => {
    println(r)
  })
  //1470383432676
  println(System.currentTimeMillis())
  Thread.sleep(1000)
}
