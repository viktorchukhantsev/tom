package tom.http.endpoints

import org.http4s.server.Router
import org.http4s.{ EntityBody, HttpRoutes }
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import sttp.tapir.{ endpoint, stringBody, Endpoint }
import zio.{ IO, RIO }
import zio.interop.catz.{ taskConcurrentInstance, zioContextShift }

final class TomApp[R] {
  type TomAppTask[A] = RIO[R, A]

  val tomEndpoint: Endpoint[Unit, String, String, Nothing] =
    endpoint.get.errorOut(stringBody).out(stringBody)

  val robotsTxtEndpoint: Endpoint[Unit, String, String, Nothing] =
    endpoint.get.errorOut(stringBody).out(stringBody)

  // extension methods for ZIO; not a strict requirement, but they make working with ZIO much nicer
  implicit class ZioEndpoint[I, E, O](e: Endpoint[I, E, O, EntityBody[TomAppTask]]) {
    def toZioRoutes(
      logic: I => IO[E, O]
    )(implicit serverOptions: Http4sServerOptions[TomAppTask]): HttpRoutes[TomAppTask] = {
      import sttp.tapir.server.http4s._
      e.toRoutes(i => logic(i).either)
    }

    def zioServerLogic(logic: I => IO[E, O]): ServerEndpoint[I, E, O, EntityBody[TomAppTask], TomAppTask] =
      ServerEndpoint(e, logic(_).either)
  }

  def tomRoutes: HttpRoutes[TomAppTask] =
    tomEndpoint.toZioRoutes(_ => LayerEndpoint.TomService.tom.provideLayer(LayerEndpoint.liveEnv))

  def robotsTxtRoutes: HttpRoutes[TomAppTask] =
    robotsTxtEndpoint.toZioRoutes(_ => LayerEndpoint.RobotsService.robots.provideLayer(LayerEndpoint.liveEnv))

  private val yaml: String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._
    List(tomEndpoint).toOpenAPI("Tom", "1.0").toYaml
  }

  def routes: HttpRoutes[TomAppTask] = Router(
    "/robots.txt" -> robotsTxtRoutes,
    "/docs"       -> new SwaggerHttp4s(yaml).routes[TomAppTask],
    "/"           -> tomRoutes
  )
}
