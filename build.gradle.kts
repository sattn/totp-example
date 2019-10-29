import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.9.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.21" // 1.2.71
	kotlin("plugin.spring") version "1.3.21" // 1.2.71
}

group = "com.takahiro310"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
}

val ktlint by configurations.creating

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("mysql:mysql-connector-java")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	ktlint("com.pinterest:ktlint:0.33.0")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.0")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.apache.httpcomponents:httpclient")
	implementation("commons-codec:commons-codec:1.10")
	implementation("dev.turingcomplete:kotlin-onetimepassword:2.0.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

task("ktlint", JavaExec::class)  {
	group = "verification"
	description = "Check Kotlin code style."
	main = "com.pinterest.ktlint.Main"
	classpath = configurations.getByName("ktlint")
	args("src/**/*.kt")
}

tasks.named("check") {
	dependsOn( ktlint )
}

task("ktlintFormat", JavaExec::class) {
	group = "formatting"
	description = "Fix Kotlin code style deviations."
	main = "com.pinterest.ktlint.Main"
	classpath = configurations.getByName("ktlint")
	args("-F", "src/**/*.kt")
}
