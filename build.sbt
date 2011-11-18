sbtPlugin := true

name := "deployit-common-build"

organization := "com.xebialabs.deployit"

version := "0.1-SNAPSHOT"

autoScalaLibrary := false

crossPaths := false

publishTo <<= (version) { version: String =>
  val nexus = "http://dexter.xebialabs.com/nexus/content/repositories/"
  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/") 
  else                                   Some("releases"  at nexus + "releases/")
}

credentials += Credentials(Path.userHome / ".sbt" / "credentials")
