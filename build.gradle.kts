import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"

    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

springBoot {
    mainClass.set("com.example.anifoxapi.AniFoxApiApplicationKt")
}
tasks.getByName<Jar>("jar") {
    enabled = false
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

//extra["springBootAdminVersion"] = "2.7.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator:2.1.3.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-web:2.1.3.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.1.3.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-mail:2.1.3.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-security:2.1.3.RELEASE")
    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    // Swagger
    implementation("org.springdoc:springdoc-openapi-data-rest:1.6.9")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.9")
    // Beautiful Logger
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("it.skrape:skrapeit:1.2.2")
    // Selenium WebDrive
    implementation("org.seleniumhq.selenium:selenium-java:4.2.1")

    // Chrome Driver for Selenium
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.2.1")

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.56")

    implementation("com.google.guava:guava:31.1-jre")

    implementation("org.springframework.boot:spring-boot-starter-web")


    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.1.3.RELEASE")
    implementation("commons-io:commons-io:2.4")
    implementation("io.jsonwebtoken:jjwt:0.9.0")
    implementation("io.jsonwebtoken:jjwt-api:0.10.6")
    implementation("com.mashape.unirest:unirest-java:1.4.9")
//    implementation("de.codecentric:spring-boot-admin-starter-client")
//    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.projectlombok:lombok:1.18.22")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
}

//dependencyManagement {
//    imports {
//        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
//    }
//}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "18"
    }
    kotlinOptions.jvmTarget = "18"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
