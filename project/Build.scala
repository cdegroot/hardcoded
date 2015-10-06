import sbt._
import sbt.Keys._

object HardcodedBuild extends Build {

  lazy val root = Project(id = "hardcoded", base = file("."))
    .configs(IntegrationTest)
    .settings(
      scalaVersion := "2.11.7",
      crossScalaVersions := Seq("2.10.6", "2.11.7"),
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scalatest" %% "scalatest" % "2.2.4" % "it,test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "it,test"))

}
