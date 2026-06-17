plugins {
    kotlin("jvm") version "2.2.21"
    alias(libs.plugins.jacoco)
    alias(libs.plugins.detekt)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.sqlite.jdbc)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

// Configuración de JaCoCo
jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
    // Esto es importante para que SonarQube encuentre el informe
    executionData.setFrom(fileTree(layout.buildDirectory.get().asFile).include("jacoco/*.exec"))
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}
