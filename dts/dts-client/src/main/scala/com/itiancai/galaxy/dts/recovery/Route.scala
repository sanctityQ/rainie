package com.itiancai.galaxy.dts.recovery

import com.twitter.finagle.http.{Response, Request}
import com.twitter.util.Future

case class Route(name: String, path: String, callback: Request => Future[Response])
