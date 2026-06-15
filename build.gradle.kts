plugins {
    kotlin("jvm") version "2.2.21"
    alias(libs.plugins.jacoco)
    //alias(libs.plugins.detekt) // Aplicamos el plugin de Detekt usando alias
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val detekt by configurations.creating

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.sqlite.jdbc)
    testImplementation(libs.mockk) // Añadimos la dependencia de MockK para tests
    detekt("dev.detekt:detekt-cli:2.0.0-alpha.4")
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
    executionData.setFrom(fileTree(layout.buildDirectory.get().asFile).include("jacoco/*.exec"))
}

//detekt {
//    toolVersion = "2.0.0-alpha.4"
//    config.setFrom(file("config/detekt/detekt.yml"))
//    buildUponDefaultConfig = true
//}


val detektTask = tasks.register<JavaExec>("detekt") {
    mainClass.set("dev.detekt.cli.Main")
    classpath = detekt

    val input = projectDir
    val config = "config/detekt/detekt.yml"
    val exclude = ".*/build/.*,.*/resources/.*"
    val params = listOf("-i", input, "-c", config, "-ex", exclude)

    args(params)
}

// Remove this block if you don't want to run detekt on every build
tasks.check {
    dependsOn(detektTask)
}
