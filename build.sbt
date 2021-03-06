import com.typesafe.config.ConfigFactory
import rocks.muki.graphql.codegen.CodeGenStyles
import sbt.{ Def, Resolver, _ }
//settings

name := """todo-rest-graphql-sample"""
scalaVersion := Dependencies.scalaVersion

resolvers += Resolver.githubPackages("innFactory")

val token = sys.env.getOrElse("GITHUB_TOKEN", "")

val githubSettings = Seq(
  githubOwner := "innFactory",
  credentials :=
    Seq(
      Credentials(
        "GitHub Package Registry",
        "maven.pkg.github.com",
        "innFactory",
        token
      )
    )
)

val generatedFilePath: String = "/dbdata/Tables.scala"
val flywayDbName: String      = "todo-rest-graphql-sample"
val dbConf                    = settingKey[DbConf]("Typesafe config file with slick settings")
val generateTables            = taskKey[Seq[File]]("Generate slick code")

// Testing

coverageExcludedPackages += "<empty>;graphql.codegen*;Reverse.*;router.*;.*AuthService.*;models\\\\.data\\\\..*;dbdata.Tables*;todorestgraphqlsample.common.jwt.*;todorestgraphqlsample.common.errorHandling.*;todorestgraphqlsample.common.jwt.JwtFilter;db.codegen.*;todorestgraphqlsample.common.pubSub.*;publicmetrics.influx.*"
Test / fork := true

// Commands

addCommandAlias("testsWithCov", "; clean; coverage; flyway/flywayMigrate; graphqlCodegen; test; coverageReport")
addCommandAlias("tests", "; clean; flyway/flywayMigrate; graphqlCodegen; test")

/* TaskKeys */
lazy val slickGen = taskKey[Seq[File]]("slickGen")

/* Create db config for flyway */
def createDbConf(dbConfFile: File): DbConf = {
  val configFactory = ConfigFactory.parseFile(dbConfFile)
  val configPath    = s"$flywayDbName"
  val config        = configFactory.getConfig(configPath).resolve
  val url           = s"${config.getString("database.urlPrefix")}${config
    .getString("database.host")}:${config.getString("database.port")}/${config.getString("database.db")}"
  DbConf(
    config.getString("profile"),
    config.getString("database.driver"),
    config.getString("database.user"),
    config.getString("database.password"),
    url
  )
}

def dbConfSettings =
  Seq(
    Global / dbConf := createDbConf((Compile / resourceDirectory).value / "application.conf")
  )

def flywaySettings =
  Seq(
    flywayUrl := (Global / dbConf).value.url,
    flywayUser := (Global / dbConf).value.user,
    flywayPassword := (Global / dbConf).value.password,
    flywaySchemas := (Seq("postgis"))
  )

def generateTablesTask(conf: DbConf) =
  Def.task {
    val dir          = sourceManaged.value
    val outputDir    = (dir / "slick/main").getPath
    val fname        = outputDir + generatedFilePath
    val generator    = "db.codegen.CustomizedCodeGenerator"
    val url          = conf.url
    val slickProfile = conf.profile.dropRight(1)
    val jdbcDriver   = conf.driver
    val pkg          = "db.Tables"
    val cp           = (Compile / dependencyClasspath).value
    val username     = conf.user
    val password     = conf.password
    val s            = streams.value
    val r            = (Compile / runner).value
    r.run(
      generator,
      cp.files,
      Array(outputDir, slickProfile, jdbcDriver, url, pkg, username, password),
      s.log
    )
    Seq(file(fname))
  }

slickGen := Def.taskDyn(generateTablesTask((Global / dbConf).value)).value

/*project definitions*/

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, GraphQLSchemaPlugin, GraphQLQueryPlugin, GraphQLCodegenPlugin)
  .dependsOn(slick)
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    dbConfSettings,
    libraryDependencies ++= Dependencies.list,
    // Adding Cache
    libraryDependencies ++= Seq(ehcache),
    dependencyOverrides += Dependencies.sl4j, // Override to avoid problems with HikariCP 4.x
    githubSettings
  )

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    flywaySettings,
    githubSettings
  )

lazy val slick = (project in file("modules/slick"))
  .settings(
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    githubSettings
  )

lazy val globalResources = file("conf")

/* Scala format */
ThisBuild / scalafmtOnCompile := true // all projects

/* sbt-graphql configure schema generation */
graphqlSchemaSnippet := "todorestgraphqlsample.graphql.schema.SchemaDefinition.graphQLSchema"
graphqlSchemaGen / target := baseDirectory.value

/* sbt-graphql configure code generation */
graphqlSchemas += GraphQLSchema(
  "todoSchema",
  "The schema to generate code from",
  Def
    .task(
      GraphQLSchemaLoader
        .fromFile(baseDirectory.value / "schema.graphql")
        .loadSchema()
    )
    .taskValue
)
graphqlCodegenSchema := graphqlRenderSchema.toTask("todoSchema").value
Compile / graphqlCodegen / sourceDirectories := Seq(baseDirectory.value / "test/resources")
Test / graphqlCodegen / sourceDirectories := Seq(baseDirectory.value / "test/resources")
graphqlCodegen / excludeFilter := HiddenFileFilter || "*.fragment.graphql" || "schema.graphql"

/* configure src_managed as Generated sources root to be able to import generated code */
lazy val scalaVersionFirstTwo = """[\d]*[\.][\d]*""".r.findFirstIn(Dependencies.scalaVersion).get
Compile / managedSourceDirectories += baseDirectory.value / s"target/scala-${scalaVersionFirstTwo}/src_managed"

/* Change compiling */
Compile / sourceGenerators += Def.taskDyn(generateTablesTask((Global / dbConf).value)).taskValue
Compile / compile := {
  (Compile / compile).value
}
