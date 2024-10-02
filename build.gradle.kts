plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "com.fintech"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	implementation("io.ktor:ktor-client-cio:2.3.12")
	implementation("io.ktor:ktor-client-okhttp:2.3.12")
	implementation("io.ktor:ktor-client-logging:2.3.12")
	implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.apache.commons:commons-csv:1.10.0")
	implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
	implementation("org.slf4j:slf4j-simple:1.7.32")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}