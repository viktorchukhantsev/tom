package tom.environment

import tom.environment.config.Configuration.HttpServerConfig
import zio.Has

package object config {
  type Configuration = Has[HttpServerConfig]
}
