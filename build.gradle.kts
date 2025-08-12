import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "tech.jaya"
version = "0.0.1-SNAPSHOT"

val flywayVersion = "10.10.0"
val springCloudVersion = "2023.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-devtools")

	// Kotlin & Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	// Database
	implementation("com.mysql:mysql-connector-j:8.4.0")
	implementation("org.flywaydb:flyway-core:$flywayVersion")
	runtimeOnly("org.flywaydb:flyway-mysql:$flywayVersion")
	runtimeOnly("com.h2database:h2")

	// Feign Client
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	implementation("org.hibernate.orm:hibernate-spatial:6.4.6.Final")
	runtimeOnly("com.mysql:mysql-connector-j:8.4.0")


	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito", module = "mockito-core") // evita duplicidade
	}
	testImplementation("org.mockito:mockito-core:5.11.0")
	testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.test {
	useJUnitPlatform()
}

