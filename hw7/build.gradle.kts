plugins {
    jacoco
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.kapt") version "2.0.20"
}

group = "com.fintech"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

kapt {
    keepJavacAnnotationProcessors = true
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "IGNORE")
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = false
        csv.required = true
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("com/fintech/kotlin/dto/**")
            }
        })
    )
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    kapt("org.mapstruct:mapstruct-processor:1.6.2")
    implementation("org.mapstruct:mapstruct:1.6.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation ("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:1.0-alpha-13")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")


	//testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	//testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
//	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
//	testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("com.github.tomakehurst:wiremock:3.0.0-beta-8")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}