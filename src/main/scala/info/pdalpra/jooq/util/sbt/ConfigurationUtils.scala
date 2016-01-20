package info.pdalpra.jooq.util.sbt

import sbt._

import scala.xml.Elem

import info.pdalpra.jooq.util.sbt.model._

import scalaxb.CanWriteXML

object ConfigurationUtils {

  private def marshalToString[T: CanWriteXML](obj: T, label: String) = scalaxb.toXML(obj, label, defaultScope).toString()

  def writeConfigFile(jdbc: Jdbc, generator: Generator, configFile: File, outputDirectory: File): Unit = {
    val generatorWithTarget = generator.copy(target = generator.target.map(_.copy(directory = Some(outputDirectory.getAbsolutePath))))
    val configFileContents = marshalToString(jdbc, "jdbc") + marshalToString(generatorWithTarget, "generator")
    val xmlContent = Seq("<configuration>", configFileContents, "</configuration>")
    IO.writeLines(configFile, xmlContent)
  }

}
