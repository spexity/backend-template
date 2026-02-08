plugins {
	alias(libs.plugins.kotlin.jvm)
	`java-library`
}

dependencies {
	api(libs.jakarta.validation.api)
}
