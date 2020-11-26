package tom

import tom.http.Server
import zio.{App, URIO, ZEnv, ExitCode => ZExitCode}
import zio.console.Console
import tom.environment.Environments.appEnvironment

object Cat extends App {
  def run(args: List[String]): URIO[ZEnv, ZExitCode] = {
    val program = for {
      _ <- Server.runServer
    } yield ()

    program.provideLayer(appEnvironment ++ Console.live).exitCode
  }
}
