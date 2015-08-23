import sbt._
import sbt.Keys._

object HardcodedBuild extends Build {
  //val buildScalaVersion = "2.11.7"
  val buildScalaVersion = "2.10.5"

  lazy val root = Project(id = "hardcoded", base = file("."))
    .configs(IntegrationTest)
    .settings(
      scalaVersion := buildScalaVersion,
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % buildScalaVersion,
        "org.scalatest" %% "scalatest" % "2.2.4" % "it,test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "it,test"))

}
