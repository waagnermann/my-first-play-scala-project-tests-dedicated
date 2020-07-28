name := """my-first-play-scala-project-tests-dedicated"""
organization := "com.moscoworg"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.9"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  "org.playframework.anorm" %% "anorm" % "2.6.4",
  "com.oracle.jdbc" % "ojdbc8" % "12.2.0.1",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "io.swagger" %% "swagger-play2" % "1.7.1",
  "org.webjars" % "swagger-ui" % "3.23.0",
  "org.webjars" %% "webjars-play" % "2.8.0",
    jdbc
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.moscowbrothers.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.moscowbrothers.binders._"
