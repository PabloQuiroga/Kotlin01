plugins {
    kotlin("jvm") version "2.2.21"
    alias(libs.plugins.jacoco)
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

// Configuración de JaCoCo
jacoco {
    toolVersion = "0.8.12" // La versión de la herramienta JaCoCo sí se especifica aquí
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Asegura que los tests se ejecuten antes del informe
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
    // Esto es importante para que SonarQube encuentre el informe
    executionData.setFrom(fileTree(project.buildDir).include("jacoco/*.exec"))
}
