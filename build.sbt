import Dependencies._
import BuildHelper._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.ebarrientos"
ThisBuild / organizationName := "ebarrientos"

lazy val testSettings = Seq(
  libraryDependencies ++= testDeps
)

lazy val root = (project in file("."))
  .settings(
    name := "serverbench",
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
  .settings(testSettings)
  .settings(stdSettings("root"))
  .aggregate(core, http4s, ziohttp)

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

lazy val akkahttp = (project in file("akkahttp"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= akkaHttpDeps
  )
  .settings(stdSettings("akkahttp"))

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
