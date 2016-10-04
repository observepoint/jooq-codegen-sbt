publishMavenStyle := true

lazy val nexusServer = Option(System.getProperty("nexus")).getOrElse("nexus.observepoint.com")

publishTo := {
  val nexus = "https://" + nexusServer + "/repository/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "snapshots")
  else
    Some("releases" at nexus + "releases")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")