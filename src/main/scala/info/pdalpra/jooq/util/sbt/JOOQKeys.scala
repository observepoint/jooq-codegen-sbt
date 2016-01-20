package info.pdalpra.jooq.util.sbt

import sbt._

import info.pdalpra.jooq.util.sbt.model.{Generator, Jdbc}

import scala.xml.Elem

object JOOQKeys {

  val jooqVersion = settingKey[String]("JOOQ version to use for the model generator")

  val jdbc = settingKey[Option[Jdbc]]("JDBC configuration, as a case class")
  val generator = settingKey[Option[Generator]]("Generator configuration, as a case class")

  val jooqOutputDirectory = settingKey[File]("Output directory for the generated code. Either java or scala will be appended depending on the specified code generator name")
  val showGenerationLog = settingKey[Boolean]("Controls if jOOQ generation log are printed or not")
  val configFile = settingKey[File]("jOOQ config file passed to the generator")
  val generate = taskKey[Seq[File]]("Generate jOOQ metamodel")

}
