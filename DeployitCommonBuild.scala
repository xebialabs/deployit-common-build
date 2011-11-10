import sbt._

object DeployitCommonBuild extends Plugin {

	object Dependencies {
		import Dependency._
		def excludeFromAll(seq: Seq[ModuleID]) = seq map { _.excludeAll (
			ExclusionRule(organization = "org.slf4j"),
			ExclusionRule(organization = "com.sun.xml.bind"),
			ExclusionRule(organization = "com.sun.xml.stream")
		)}

		val resteasy = excludeFromAll(Seq(resteasyJaxrs, resteasyMultipart, resteasyJaxb))
		val standardDeps = Seq(slf4jApi, guava)
		val springDeps = Seq(springCore, springSecurity)
		val testDeps = Seq(Test.junit, Test.junitInterface, Test.hamcrestCore, Test.hamcrestLib, Test.logbackClassic)
	}
	object Dependency {
		object V {
			val Api = "3.6.0-beta-14"
			val Overthere = "1.0.7"
			val Resteasy = "2.0.0.GA"
			val Slf4j = "1.6.3"
			val Spring = "3.0.5.RELEASE"
		}

		val commonsLang = "commons-lang" % "commons-lang" % "2.5"
		val deployitPluginApi = "com.xebialabs.deployit" % "udm-plugin-api" % V.Api
		val guava = "com.google.guava" % "guava" % "10.0.1"
		val jaxRsApi = "org.jboss.resteasy" % "jaxrs-api" % V.Resteasy
		val resteasyJaxrs = "org.jboss.resteasy" % "resteasy-jaxrs" % V.Resteasy 
		val resteasyMultipart = "org.jboss.resteasy" % "resteasy-multipart-provider" % V.Resteasy 
		val resteasyJaxb = "org.jboss.resteasy" % "resteasy-jaxb-provider" % V.Resteasy 
		val slf4jApi = "org.slf4j" % "slf4j-api" % V.Slf4j
		val springCore = "org.springframework" % "spring-core" % V.Spring
		val springSecurity = "org.springframework.security" % "spring-security-core" % V.Spring


		object Test {
			val junit = "junit" % "junit-dep" % "4.10" % "test"
			val junitInterface = "com.novocode" % "junit-interface" % "0.7" % "test"
			val hamcrestCore = "org.hamcrest" % "hamcrest-core" % "1.2.1" % "test"
			val hamcrestLib = "org.hamcrest" % "hamcrest-library" % "1.2.1" % "test"
			val logbackClassic = "ch.qos.logback" % "logback-classic" % "0.9.30" % "test"
		}
	}

	val deployitRepositories = Seq(
		"Dexter Nexus Public" at "http://dexter.xebialabs.com/nexus/content/groups/public"
	)
}
