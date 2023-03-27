import AssemblyKeys._

assemblySettings

name := "spark-analytics"

version := "1.1"

scalaVersion := "2.10.4"

resolvers += "conjars.org" at "http://conjars.org/repo"

resolvers += "clojars.org" at "http://clojars.org/repo"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.1.0"

libraryDependencies += "org.apache.spark" %% "spark-catalyst" % "1.1.0" exclude("org.apache.spark", "spark-core")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

libraryDependencies += "org.apache.cassandra" % "cassandra-thrift" % "2.1.2" exclude("com.google.guava", "guava")

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

//guava comes here
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.3"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "com.ning" % "compress-lzf" % "0.8.4"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.4.0" withSources()

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "1.0.2"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.10" withSources()

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "1.3.2" withSources()

libraryDependencies += "org.elasticsearch" % "elasticsearch-hadoop" % "2.1.0.BUILD-SNAPSHOT" exclude ("org.apache.hive",  "hive-exec") withSources()

libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.1.0" withSources() withJavadoc()

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf" => MergeStrategy.deduplicate
  case "unwanted.txt" => MergeStrategy.discard
  case x => old(x)
}
}