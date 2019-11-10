import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.4.RELEASE"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	kotlin("jvm") version "1.3.21"
	kotlin("plugin.jpa") version "1.3.21"
	kotlin("plugin.spring") version "1.3.21"
	id("org.asciidoctor.convert") version "1.5.3"
	id("idea")
}

group = "com.jonginout"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

val snippetsDir = File("build/generated-snippets")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.cloud:spring-cloud-starter-oauth2:2.1.4.RELEASE")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testRuntimeOnly("com.h2database:h2")
	runtimeOnly("mysql:mysql-connector-java")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.test {
	outputs.dir(snippetsDir)
}

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	dependsOn("test")
}

tasks.asciidoctor {
	doFirst {
		val docDir = File("src/main/resources/static/docs")
		if (docDir.exists()) {
			delete(docDir.listFiles())
		}
	}
}

tasks.asciidoctor {
	doLast {
		copy {
			from("build/asciidoc/html5")
			into("src/main/resources/static/docs")
		}
	}
}

tasks.build {
	dependsOn("asciidoctor")
}
