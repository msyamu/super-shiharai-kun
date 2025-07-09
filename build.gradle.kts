
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.9.23"
}

tasks.test {
    useJUnitPlatform()
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Ktor ---
    implementation("io.ktor:ktor-server-core:3.2.1")
    implementation("io.ktor:ktor-server-netty:3.2.1")
    implementation("io.ktor:ktor-server-auth:3.2.1")
    implementation("io.ktor:ktor-server-auth-jwt:3.2.1")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("io.ktor:ktor-server-status-pages:3.2.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.1")
    implementation("io.ktor:ktor-server-call-logging:3.2.1")
    implementation("io.ktor:ktor-server-config-yaml:3.2.1")
    implementation("io.ktor:ktor-server-openapi:3.2.1")
    implementation("io.ktor:ktor-server-swagger:3.2.1")
    implementation("io.ktor:ktor-server-cors:3.2.1")

    // BCrypt
    implementation("org.mindrot:jbcrypt:0.4")

    // --- Exposed ORM ---
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")

    // --- PostgreSQL JDBC Driver ---
    implementation("org.postgresql:postgresql:42.7.7")

    // --- Logging ---
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // --- Testing ---
    testImplementation("io.ktor:ktor-server-test-host:3.2.1")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.23")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("com.h2database:h2:2.2.224")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
