import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    val kotlinVersion = "2.0.0"
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.hibernate.orm") version "6.6.8.Final"
    id("io.sentry.jvm.gradle") version "5.5.0"
}

allOpen {
    annotation("javax.persistence.Entity")
}

group = "grantly"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.mysql:mysql-connector-j")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.9.10")
    implementation("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations")
    implementation("io.viascom.nanoid:nanoid:1.0.1")
    implementation("aws.sdk.kotlin:ses:1.4.86") {
        exclude("com.squareup.okhttp3:okhttp")
    }
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("org.apache.tika:tika-core:3.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("me.atrox.haikunator:Haikunator:2.0.1")

    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    // For multi arch (Apple Silicon) support
    builder.set("paketobuildpacks/builder-jammy-buildpackless-tiny")
    buildpacks.set(listOf("paketobuildpacks/java"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-Xshare:off")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("api")
    archiveVersion.set("1.0.0")
    archiveClassifier.set("")
}

sentry {
    includeSourceContext = true
    org = "team-unchained"
    projectName = "grantly-backend"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}
