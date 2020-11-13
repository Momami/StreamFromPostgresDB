name := "PostgreStream"

version := "0.1"

scalaVersion := "2.12.11"

lazy val akkaHttpVersion = "10.2.1"
lazy val akkaVersion = "2.6.10"
resolvers += Resolver.bintrayRepo("krasserm", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.tpolecat" %% "doobie-postgres" % "0.9.0",
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.github.krasserm" %% "streamz-converter" % "0.10-M2"
)