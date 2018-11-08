import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

val defaultDockerRegistry = "261496907632.dkr.ecr.us-east-2.amazonaws.com"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "dazn-agnostic-coding-challenge",
    version := "1",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.specs2"         %% "specs2-core"            % "4.1.0" % "test",
      "com.twitter"        %% "finagle-http"           % "18.10.0",
      "com.github.finagle" %% "finagle-http-auth"      % "0.1.0",
      "ch.qos.logback"     %  "logback-classic"        % "1.2.3"
    ),
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(8080),
    dockerRepository := Some(sys.env.getOrElse("DOCKER_REGISTRY", defaultDockerRegistry)),
  )
