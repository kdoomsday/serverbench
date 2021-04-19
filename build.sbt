import Dependencies._
import BuildHelper._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.ebarrientos"
ThisBuild / organizationName := "ebarrientos"

lazy val testSettings = Seq(
  libraryDependencies ++= testDeps
)

lazy val root = (project in file("."))
  .settings(
    name := "serverbench",
    libraryDependencies += scalaTest % Test,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
  .settings(stdSettings("root"))

// --- Data project ------------------------------------------------------------
lazy val core = (project in file("core"))
  .settings(libraryDependencies ++= zioDeps)
  .settings(libraryDependencies ++= circeDeps)
  .settings(testSettings)
  .settings(stdSettings("core"))
  .settings(libraryDependencies ++= loggingDeps)


// --- Server projects ---------------------------------------------------------
lazy val http4s = (project in file("http4s"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= http4sDeps
  )
  .settings(stdSettings("http4s"))

lazy val ziohttp = (project in file("ziohttp"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= zioDeps,
    libraryDependencies ++= zioHttp
  )
  .settings(stdSettings("ziohttp"))


// --- Dependencies ------------------------------------------------------------
// Testing
lazy val testDeps = Seq(
  "org.scalactic"     %% "scalactic"       % "3.2.5",
  "org.scalatest"     %% "scalatest"       % "3.2.5"   % "test",
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.5.0" % "test"
)

// Circe
val circeVersion = "0.13.0"
lazy val circeDeps = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// ZIO
val zioVersion     = "1.0.5"
val zioCatsVersion = "2.2.0.1"
lazy val zioDeps = Seq(
  "dev.zio" %% "zio"              % zioVersion,
  "dev.zio" %% "zio-interop-cats" % zioCatsVersion
)

// Http4s
val http4sVersion   = "0.21.15"
lazy val http4sDeps = Seq(
  "org.http4s" %% "http4s-blaze-server",
  "org.http4s" %% "http4s-blaze-client",
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-dsl",
  "org.http4s" %% "http4s-twirl"
).map(_ % http4sVersion)


// ZIO http
val zioHttpVersion = "1.0.0.0-RC15"
lazy val zioHttp = Seq(
  "io.d11"  %% "zhttp"            % zioHttpVersion
)

// Logging
val scalaLoggingVersion = "3.9.3"
val logbackVersion = "1.2.3"
lazy val loggingDeps = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
