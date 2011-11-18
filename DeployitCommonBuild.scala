import sbt._
import Keys._

object PluginBuild extends Plugin {
  val Itest = config("itest") extend(Test)

  lazy val pluginSettings : Seq[sbt.Project.Setting[_]] = inConfig(Itest)(Defaults.testTasks) ++ Seq(
    testOptions in Test := Seq(Tests.Filter(_.endsWith("Test"))),
    testOptions in Itest := Seq(Tests.Filter(_.endsWith("Itest")))
  )
}

object DeployitCommonBuild extends Plugin {
  val nexus = "http://dexter.xebialabs.com/nexus/content/"

  def itest(projectRefs: Seq[Project]) = TaskKey[Unit]("itest","Run Itests") <<= projectRefs.map(ref => test in PluginBuild.Itest in ref).dependOn

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.xebialabs.deployit",
    autoScalaLibrary := false,
    crossPaths := false,
    shellPrompt := ShellPrompt.buildShellPrompt,
    resolvers ++= deployitRepositories,
    libraryDependencies ++= Dependencies.standardDeps ++ Dependencies.testDeps,
    parallelExecution in Test := false,
    logLevel := Level.Info,
    ivyLoggingLevel := UpdateLogging.Quiet,
    credentials += Credentials(Path.userHome / ".sbt" / "credentials"),
    publishTo <<= (version) { _ match {
      case x if x.endsWith("SNAPSHOT") => Some("snapshots" at nexus + "repositories/snapshots/")
      case _ => Some("releases" at nexus + "repositories/releases/")
    } }
  )
 
  object PluginProject {
    import PluginBuild._
    def apply(name: String, settings: Seq[Setting[_]]): Project = PluginProject(name, file(name), settings)
    def apply(name: String, file: File, settings: Seq[Setting[_]]) = Project(
      name, 
      file,
      settings = buildSettings ++ pluginSettings ++ Seq(
        organization := "com.xebialabs.deployit.plugins"
      ) ++ settings
    ) configs(Itest)
  }
  
  object Dependencies {
    import Dependency._
    def excludeFromAll(seq: Seq[ModuleID]) = seq map { _.excludeAll (
      ExclusionRule(organization = "org.slf4j"),
      ExclusionRule(organization = "com.sun.xml.bind"),
      ExclusionRule(organization = "com.sun.xml.stream"),
      ExclusionRule(organization = "junit")
    )}

    val jackrabbit = Seq(jackrabbitApi, jackrabbitCore)
    val resteasy = excludeFromAll(Seq(resteasyJaxrs, resteasyMultipart, resteasyJaxb))
    val standardDeps = Seq(slf4jApi, guava)
    val springDeps = Seq(springCore, springSecurity)
    val testDeps = Seq(Test.junit) ++ excludeFromAll(Seq(Test.junitInterface, Test.hamcrestCore, Test.hamcrestLib, Test.logbackClassic, Test.scalaTest))
    val truezip = Seq(truezipFile, truezipKernel, truezipDriverFile)
  }

  object Dependency {
    object V {
      val Api = "3.6.0-beta-14"
      val Jackrabbit = "2.2.0"
      val Jython = "2.5.1"
      val Overthere = "1.0.7"
      val Resteasy = "2.0.0.GA"
      val Slf4j = "1.6.3"
      val Spring = "3.0.5.RELEASE"
      val Truezip = "7.3.4"
    }

    val args4j = "args4j" % "args4j" % "2.0.16"
    val commonsLang = "commons-lang" % "commons-lang" % "2.5"
    val deployitJeePlugin = "com.xebialabs.deployit.plugins" % "jee-plugin" % V.Api
    val deployitOvertherePlugin = "com.xebialabs.deployit.plugins" % "overthere-plugin" % V.Api
    val deployitPluginApi = "com.xebialabs.deployit" % "udm-plugin-api" % V.Api
    val deployitPlanner = "com.xebialabs.deployit" % "planner" % V.Api
    val deployitPythonPlugin = "com.xebialabs.deployit.plugins" % "python-plugin" % V.Api
    val deployitUdmServerApi = "com.xebialabs.deployit" % "udm-server-api" % V.Api
    val freemarker = "org.freemarker" % "freemarker" % "2.3.18"
    val guava = "com.google.guava" % "guava" % "10.0.1"
    val jackrabbitApi = "org.apache.jackrabbit" % "jackrabbit-api" % V.Jackrabbit
    val jackrabbitCore = "org.apache.jackrabbit" % "jackrabbit-core" % V.Jackrabbit
    val jaxRsApi = "org.jboss.resteasy" % "jaxrs-api" % V.Resteasy
    val jcr = "javax.jcr" % "jcr" % "2.0"
    val jmustache = "com.samskivert" % "jmustache" % "1.3"
    val jython = "org.python" % "jython" % V.Jython
    val jythonStandalone = "org.python" % "jython-standalone" % V.Jython
    val overthere = "com.xebialabs.overthere" % "overthere" % V.Overthere
    val pegdown = "org.pegdown" % "pegdown" % "1.0.2"
    val qrmediaCommons = "com.qrmedia.commons" % "commons-lang" % "1.0.2"
    val resteasyJaxrs = "org.jboss.resteasy" % "resteasy-jaxrs" % V.Resteasy 
    val resteasyMultipart = "org.jboss.resteasy" % "resteasy-multipart-provider" % V.Resteasy 
    val resteasyJaxb = "org.jboss.resteasy" % "resteasy-jaxb-provider" % V.Resteasy 
    val slf4jApi = "org.slf4j" % "slf4j-api" % V.Slf4j
    val scannit = "nl.javadude.scannit" % "scannit" % "0.13"
    val springCore = "org.springframework" % "spring-core" % V.Spring
    val springSecurity = "org.springframework.security" % "spring-security-core" % V.Spring
    val truezipFile = "de.schlichtherle.truezip" % "truezip-file" % V.Truezip
    val truezipDriverFile = "de.schlichtherle.truezip" % "truezip-driver-file" % V.Truezip
    val truezipDriverZip = "de.schlichtherle.truezip" % "truezip-driver-zip" % V.Truezip
    val truezipKernel = "de.schlichtherle.truezip" % "truezip-kernel" % V.Truezip

    object Test {
      val deployitUdmTestSupport = "com.xebialabs.deployit" % "udm-test-support" % V.Api % "test"
      val itestSupport = "com.xebialabs.overthere" % "itest-support" % V.Overthere
      val junit = "junit" % "junit-dep" % "4.10" % "test"
      val junitInterface = "com.novocode" % "junit-interface" % "0.7" % "test"
      val hamcrestCore = "org.hamcrest" % "hamcrest-core" % "1.2.1" % "test"
      val hamcrestLib = "org.hamcrest" % "hamcrest-library" % "1.2.1" % "test"
      val logbackClassic = "ch.qos.logback" % "logback-classic" % "0.9.30" % "test"
      val scalaTest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
      val mockito = "org.mockito" % "mockito-all" % "1.8.5" % "test"
    }
  }

  val deployitRepositories = Seq(
    "Local Maven2 Repo" at Path.userHome + ".m2/repository",
    "Dexter Nexus Public" at nexus + "groups/public",
    "Dexter Nexus Releases" at nexus + "repositories/releases"
  )

  object ShellPrompt {
    object devnull extends ProcessLogger {
      def info(s: => String) {}
      def error(s: => String) { }
      def buffer[T](f: => T): T = f
    }
    def currBranch = (
      ("git status -sb" lines_! devnull headOption)
        getOrElse "-" stripPrefix "## "
    )
  
    val buildShellPrompt = { 
      (state: State) => {
        val currProject = Project.extract(state).currentProject.id
        "%s:%s> ".format(currProject, currBranch)
      }
    }
  }
}
