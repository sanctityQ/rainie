package com.itiancai.galaxy.dts.recovery

import com.itiancai.galaxy.http.internal.server.BaseHttpServer
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status, Version}
import com.twitter.util.Future

import scala.collection.mutable.ArrayBuffer


trait RecoveryService extends BaseHttpServer { self =>

  val response404 = Response(Version.Http11, Status.NotFound)

  val routers = ArrayBuffer[Route]()


  def add(route: Route) = {
    routers += route
    self
  }

  var routerBuilder: Routers = null

  override def warmup(){

    super.warmup()
    val activity = injector.instance[RecoveryBuilder]


    add(Route("activity", "/dts/activity", { request =>
      //TODO 校验
      val businessType = request.getParam("businessType")
      val businessId = request.getParam("businessId")

      val response = Response(Version.Http11, Status.Ok)
      val result = activity.getActivityStateResolver(businessType).isDone(businessId)
      response.setContentString(String.valueOf(result))
      Future.value(response)
    }))

    add(Route("activity", "/dts/action", { request =>
      //TODO 校验
      val name = request.getParam("name")
      val actionMethod = request.getParam("method")
      val instructionId = request.getParam("id")
      val handler = activity.getActionServiceHandler(name)
      var result: Any = None
      actionMethod match {
        case "commit" => result = handler.commit(instructionId)
        case "rollback" => result = handler.rollback(instructionId)
        case _ => {
          response404
        }
      }
      val response = Response(Version.Http11, Status.Ok)
      response.contentString = String.valueOf(result)
      Future.value(response)
    }))

    routerBuilder = new Routers(routers.toArray)
    logger.info("RecoveryService start success")
  }

  override final def httpService: Service[Request, Response] = {
    new Service[Request, Response] {
      override def apply(request: Request): Future[Response] = {
        routerBuilder.handle(request).get
      }
    }
  }
}