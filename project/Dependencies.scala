import sbt._

object Dependencies {
  val zioHttpVersion      = "1.0.0.0-RC23"
  val zioVersion          = "1.0.13"
  val zioCatsVersion      = "2.2.0.1"
  val circeVersion        = "0.13.0"
  val http4sVersion       = "0.21.15"
  val scalaLoggingVersion = "3.9.3"
  val logbackVersion      = "1.2.3"
  val akkaVersion         = "2.6.8"
  val akkaHttpVersion     = "10.2.7"

  // ZIO http
  lazy val zioHttp = Seq(
    "io.d11" %% "zhttp" % zioHttpVersion
  )

  // Testing
  lazy val testDeps = Seq(
    "org.scalactic"     %% "scalactic"       % "3.2.5",
    "org.scalatest"     %% "scalatest"       % "3.2.5"   % "test",
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.5.0" % "test"
  )

  // Circe
  lazy val circeDeps = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  // ZIO
  lazy val zioDeps   = Seq(
    "dev.zio" %% "zio"              % zioVersion,
    "dev.zio" %% "zio-interop-cats" % zioCatsVersion
  )

  // Http4s
  lazy val http4sDeps   = Seq(
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-twirl"
  ).map(_ % http4sVersion)

  // Akka Http
  lazy val akkaHttpDeps = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http"        % akkaHttpVersion
  )

  // Logging
  lazy val loggingDeps = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"   % scalaLoggingVersion,
    "ch.qos.logback"              % "logback-classic" % logbackVersion
  )
}
