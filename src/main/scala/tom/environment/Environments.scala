package tom.environment

import tom.environment.config.Configuration
import zio.ULayer
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {
  type HttpServerEnvironment = Configuration with Clock with Blocking
  type AppEnvironment        = HttpServerEnvironment

  val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live >+> Blocking.live ++ Clock.live

  val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment
}
