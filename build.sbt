resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

enablePlugins(JavaServerAppPackaging)

lazy val commonSettings = Seq(
  organization := "dev.viktor",
  name := "tom",
  version := "1.0.0",
  scalaVersion := "2.13.3",
  maxErrors := 3,
  zioDeps,
  httpDeps,
  tapirDeps,
  slf4j,
  dropwizardMetrics,
  pureconfig,
  testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
// Refine scalac params from tpolecat
  scalacOptions --= Seq(
    "-Xfatal-warnings"
  ),
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
      MergeStrategy.singleOrError
    case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
    case "module-info.class"               => MergeStrategy.discard
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

lazy val catsDeps = libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % Version.cats
)

lazy val httpDeps = libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core"               % Version.http4s,
  "org.http4s" %% "http4s-dsl"                % Version.http4s,
  "org.http4s" %% "http4s-blaze-server"       % Version.http4s,
  "org.http4s" %% "http4s-circe"              % Version.http4s,
  "org.http4s" %% "http4s-dropwizard-metrics" % Version.http4s
)

lazy val dropwizardMetrics = libraryDependencies ++= Seq(
  "io.dropwizard.metrics" % "metrics-core"         % Version.dropwizardMetrics,
  "io.dropwizard.metrics" % "metrics-healthchecks" % Version.dropwizardMetrics
)

lazy val tapirDeps = libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"        % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir
)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"               % Version.zio,
  "dev.zio" %% "zio-test"          % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt"      % Version.zio % "test",
  "dev.zio" %% "zio-interop-cats"  % Version.zioInteropCats,
  "dev.zio" %% "zio-logging-slf4j" % Version.zioLogging,
  "dev.zio" %% "zio-streams"       % Version.zio
)

lazy val slf4j = libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api"    % Version.slf4j,
  "org.slf4j" % "slf4j-simple" % Version.slf4j
)

lazy val pureconfig = libraryDependencies += "com.github.pureconfig" %% "pureconfig" % Version.pureconfig

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    mainClass in assembly := Some("tom.Cat"),
    assemblyJarName in assembly := "tom-1.0.0.jar"
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("ass", "assembly")
addCommandAlias("tom", "universal:packageXzTarball")

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.4"
