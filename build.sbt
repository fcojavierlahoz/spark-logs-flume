import AssemblyKeys._ // put this at the top of the file

name := "Spark-Flume"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" %% "spark-streaming" % "1.6.1",
  "org.apache.spark" %% "spark-streaming-flume" % "1.6.1",
  "org.apache.hadoop" % "hadoop-client" % "2.7.1",
  "org.apache.hadoop" % "hadoop-hdfs" % "2.7.1",
  "org.json" % "json" % "20090211"
)

resolvers ++= Seq(
  "clojars" at "https://clojars.org/repo",
  "conjars" at "http://conjars.org/repo"
)



resourceDirectory in Compile := baseDirectory.value / "resources"

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
