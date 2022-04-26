name := "CSYE7200-Team6Project"

version := "0.1"

scalaVersion := "2.12.15"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.1.3"

libraryDependencies += "org.apache.spark" %% "spark-tags" % "3.1.3"

libraryDependencies += "io.github.takke" % "jp.takke.twitter4j-v2" % "1.1.0"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.6"

libraryDependencies += "org.apache.bahir" %% "spark-streaming-twitter" % "2.4.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.3"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.1.3"

libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"

val Http4sVersion = "1.0.0-M21"
val CirceVersion = "0.14.0-M5"
libraryDependencies ++= Seq(
  "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"      %% "http4s-circe"        % Http4sVersion,
  "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
  "io.circe"        %% "circe-generic"       % CirceVersion,
)
