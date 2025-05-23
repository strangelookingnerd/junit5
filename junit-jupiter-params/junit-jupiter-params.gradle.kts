import junitbuild.extensions.javaModuleName

plugins {
	id("junitbuild.kotlin-library-conventions")
	id("junitbuild.shadow-conventions")
	id("junitbuild.jmh-conventions")
	`java-test-fixtures`
	alias(libs.plugins.extraJavaModuleInfo)
}

description = "JUnit Jupiter Params"

dependencies {
	api(platform(projects.junitBom))
	api(projects.junitJupiterApi)

	compileOnlyApi(libs.apiguardian)

	shadowed(libs.univocity.parsers)

	compileOnly(kotlin("stdlib"))

	osgiVerification(projects.junitJupiterEngine)
	osgiVerification(projects.junitPlatformLauncher)
}

extraJavaModuleInfo {
	automaticModule(libs.univocity.parsers, "univocity.parsers")
	failOnMissingModuleInfo = false
}

tasks {
	jar {
		bundle {
			val version = project.version
			bnd("""
				Require-Capability:\
					org.junit.platform.engine;\
						filter:='(&(org.junit.platform.engine=junit-jupiter)(version>=${'$'}{version_cleanup;$version})(!(version>=${'$'}{versionmask;+;${'$'}{version_cleanup;$version}})))';\
						effective:=active
			""")
		}
	}
	shadowJar {
		relocate("com.univocity", "org.junit.jupiter.params.shadow.com.univocity")
		from(projectDir) {
			include("LICENSE-univocity-parsers.md")
			into("META-INF")
		}
	}
	compileJava {
		options.compilerArgs.addAll(listOf(
			"--add-modules", "univocity.parsers",
			"--add-reads", "${javaModuleName}=univocity.parsers"
		))
	}
	javadoc {
		(options as StandardJavadocDocletOptions).apply {
			addStringOption("-add-modules", "univocity.parsers")
			addStringOption("-add-reads", "${javaModuleName}=univocity.parsers")
		}
	}
}
