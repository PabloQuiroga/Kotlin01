plugins {
    kotlin("jvm") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.sqlite.jdbc)
    testImplementation(libs.mockk) // Añadimos la dependencia de MockK para tests
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}