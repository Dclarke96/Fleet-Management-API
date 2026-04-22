plugins {
    java
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.dylanclarke"
version = "0.0.1-SNAPSHOT"
description = "Fleet Management REST API"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // --- Spring Boot Starters ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    // --- JWT Authentication ---
    implementation ("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // --- Database Drivers ---
    runtimeOnly("com.h2database:h2")              // In-memory dev/test DB
    runtimeOnly("com.mysql:mysql-connector-j")   // Production DB option


    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.jayway.jsonpath:json-path:2.8.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}