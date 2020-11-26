package tom.http.endpoints

import tom.Tom
import zio.console.Console
import zio.{ Has, ZIO, ZLayer }

object LayerEndpoint {
  type TomService    = Has[TomService.Service]
  type RobotsService = Has[RobotsService.Service]
  type AppService    = TomService with RobotsService

  object TomService {
    trait Service {
      def tom: ZIO[Any, String, String]
    }

    val live: ZLayer[Console, Nothing, Has[Service]] = ZLayer.fromFunction { _: Console =>
      new Service {
        def tom: ZIO[Any, String, String] = ZIO.succeed(Tom.ascii)
      }
    }

    def tom: ZIO[TomService, String, String] = ZIO.accessM(_.get.tom)
  }

  object RobotsService {
    trait Service {
      def robots: ZIO[Any, String, String]
    }

    val live: ZLayer[Console, Nothing, Has[Service]] = ZLayer.fromFunction { _: Console =>
      new Service {
        def robots: ZIO[Any, String, String] = ZIO.succeed(Tom.robotsTxt)
      }
    }

    def robots: ZIO[RobotsService, String, String] = ZIO.accessM(_.get.robots)
  }

  val liveEnv: ZLayer[Any, Nothing, AppService] = Console.live >>> (TomService.live ++ RobotsService.live)
}
