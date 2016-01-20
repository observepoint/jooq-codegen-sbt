package info.pdalpra.jooq.util.sbt

import sbt._
import sbt.Keys._

import info.pdalpra.jooq.util.sbt.ConfigurationUtils._
import info.pdalpra.jooq.util.sbt.model.{Generator, Jdbc}

object JOOQPlugin extends AutoPlugin {

  val autoImport = JOOQKeys

  import autoImport._

  override def requires = plugins.JvmPlugin

  override def projectSettings = JOOQSettings

  val JOOQSettings = Seq(
    libraryDependencies += "org.jooq" % "jooq-codegen" % jooqVersion.value % "provided",
    jooqOutputDirectory := (sourceManaged in Compile).value,
    showGenerationLog := true,
    jdbc := None,
    generator := None,
    configFile := target.value / "jooq" / "jooq-config.xml",
    generate := generateMetaModel(
      jdbc.value,
      generator.value,
      ((externalDependencyClasspath in Runtime).value ++ (externalDependencyClasspath in Compile).value).map(_.data),
      configFile.value,
      jooqOutputDirectory.value,
      showGenerationLog.value
    )
  )

  private def generateMetaModel(
    jdbc:            Option[Jdbc],
    generator:       Option[Generator],
    classpath:       Seq[File],
    configFile:      File,
    outputDirectory: File,
    showLog:         Boolean
  ): Seq[File] = {

    if (jdbc.isEmpty || generator.isEmpty) throw new Exception("unable to generate model due to jdbc and/or generator not set")
    val generatorType = generatorDirectory(generator.get)
    writeConfigFile(jdbc.get, generator.get, configFile, outputDirectory / generatorType)

    val dependencyClasspath = (classpath :+ configFile.getParentFile).map(_.getAbsolutePath).mkString(sys.props("path.separator"))
    val command = Seq("java", "-cp", dependencyClasspath, "org.jooq.util.GenerationTool", s"/${configFile.getName}")
    val process = Process(command)

    if (showLog) process.! else process.!(NullProcessLogger)
    (outputDirectory / generatorType ** s"*.$generatorType").get
  }

  private def generatorDirectory(generator: Generator): String = {
    generator.name.map {
      case "org.jooq.util.ScalaGenerator" => "scala"
      case "org.jooq.util.JavaGenerator"  => "java"
    }.getOrElse("java")
  }

  private val NullProcessLogger = new ProcessLogger {
    override def error(s: => String): Unit = ()

    override def buffer[T](f: => T): T = f

    override def info(s: => String): Unit = ()
  }
}
