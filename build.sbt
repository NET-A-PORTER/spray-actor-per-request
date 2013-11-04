organization in ThisBuild := "com.netaporter"

name := "spray-apr"

version := "1.0"

scalaVersion  := "2.10.0"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.1.4",
    "io.spray" % "spray-can" % "1.1-M8",
    "io.spray" % "spray-routing" % "1.1-M8",
    "org.json4s" %% "json4s-native" % "3.2.4")