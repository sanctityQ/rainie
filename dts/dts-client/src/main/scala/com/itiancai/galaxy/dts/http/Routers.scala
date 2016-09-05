package com.itiancai.galaxy.dts.http

import java.util.{HashMap => JMap}

import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future

class Routers(routers: Array[Route]) {

  private[this] val constantRouteMap: JMap[String, Route] = {
    val jMap = new JMap[String, Route]()
    for (route <- routers) {
      jMap.put(route.path, route)
    }
    jMap
  }

  def handle(request: Request): Option[Future[Response]] = {
    Some({
      val route = constantRouteMap.get(request.path)
      if (route == null)
        Future(Response(Version.Http11, Status.NotFound))
      else
        route callback request
    })
  }

}