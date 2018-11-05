import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

val Http4sVersion = "0.18.19"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

val defaultDockerRegistry = "261496907632.dkr.ecr.us-east-2.amazonaws.com/dazn-coding-challenge"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    organization := "com.example",
    name := "quickstart",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"     %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    packageName in Docker := "agnostic",
    dockerUpdateLatest in Docker := true,
    dockerExposedPorts := Seq(8080),
    dockerRepository in Docker := Some(sys.env.getOrElse("DOCKER_REGISTRY", defaultDockerRegistry)),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4"),
  )
