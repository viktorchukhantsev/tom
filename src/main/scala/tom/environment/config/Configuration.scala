package tom.environment.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{ Has, ULayer, ZIO, ZLayer }

object Configuration {
  final case class HttpServerConfig(host: String, port: Int)
  final case class AppConfig(httpServer: HttpServerConfig)

  val live: ULayer[Configuration] = ZLayer.fromEffectMany(
    ZIO
      .effect(ConfigSource.default.loadOrThrow[AppConfig])
      .map(c => Has(c.httpServer))
      .orDie
  )
}
