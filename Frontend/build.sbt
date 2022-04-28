import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

import scala.sys.process.Process


lazy val start = TaskKey[Unit]("start")

lazy val dist = TaskKey[File]("dist")

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.13.2",
      scalacOptions ++= ScalacOptions.flags,
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      libraryDependencies ++= Seq("me.shadaj" %%% "slinky-hot" % "0.6.5"),
      scalacOptions += "-Ymacro-annotations"
    )

