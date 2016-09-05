package com.itiancai.galaxy.dts.http

import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

case class Route(name: String, path: String, callback: Request => Future[Response])
