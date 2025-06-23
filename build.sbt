import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.0"

//libraryDependencies ++= Seq(
//  "org.scalaz" %% "scalaz-core" % "7.3.8"
//)

lazy val root = (project in file("."))
  .settings(
    name := "ice-exercise"
  )
