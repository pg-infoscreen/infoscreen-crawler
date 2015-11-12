name := "infoscreen-data-api"

version := "0.1.0"

scalaVersion := "2.11.6"

enablePlugins(PlayJava)

enablePlugins(SbtNativePackager)

javacOptions ++= Seq("-encoding", "UTF-8")

libraryDependencies ++= Seq(
    cache,
    "org.mongodb" % "mongodb-driver" % "3.0.1",
    "gov.nist.math" % "jama" % "1.0.3",
    "com.wordnik" %% "swagger-play2" % "1.3.12"
)

libraryDependencies += "com.kennycason" % "kumo" % "1.1"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.4.4"


javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}
