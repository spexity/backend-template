@Suppress("DSL_SCOPE_VIOLATION")
plugins {
	alias(libs.plugins.kotlin.jvm) apply false
	alias(libs.plugins.kotlin.spring) apply false
	alias(libs.plugins.spring.boot) apply false
	alias(libs.plugins.spring.dependency.management) apply false
}

allprojects {
	group = "net.spexity"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

val javaVersion = libs.versions.java.get()
val kotlinStdlib = libs.kotlin.stdlib
val kotlinTestJunit5 = libs.kotlin.test.junit5
val junitPlatformLauncher = libs.junit.platform.launcher

subprojects {
	plugins.apply("org.jetbrains.kotlin.jvm")

	extensions.configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(javaVersion)
		}
	}

	extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
		}
	}

	dependencies {
		add("implementation", kotlinStdlib)
		add("testImplementation", kotlinTestJunit5)
		add("testRuntimeOnly", junitPlatformLauncher)
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
