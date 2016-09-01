package com.itiancai.galaxy.dts.server

import com.twitter.conversions.time._
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Version}

object HttpClientTest extends App {

  val client = Http.client.withRequestTimeout(42.seconds).newService(":8080")
  val request = Request(Version.Http11, Method.Get, "/dts/activity")
//  request.headerMap.add("User-Agent", "Finagle 0.0")
//  request.headerMap.add("Host", "127.0.0.1")


  client(request).onSuccess(r => {
    println(r)
  })
  //1470383432676
  println(System.currentTimeMillis())
  Thread.sleep(1000)
}
