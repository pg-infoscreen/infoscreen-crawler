name := "infoscreen-data-crawling"

version := "0.1.0"

scalaVersion := "2.11.5"

enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "joda-time" % "joda-time" % "2.7",
  "org.mongodb" % "mongo-java-driver" % "3.0.1"
)

libraryDependencies += "com.google.code.gson" % "gson" % "2.3.1"

javacOptions ++= Seq("-encoding", "UTF-8")

// Configuration for building RPM files
enablePlugins(RpmPlugin)

rpmRelease := "1"

rpmVendor := "PG Infoscreen"

rpmUrl := Some("http://data.pg-infoscreen.de")

rpmLicense := Some("LGPLv3+")
