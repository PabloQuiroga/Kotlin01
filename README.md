# Kotlin01: Fundamentos y Prácticas de Kotlin

Este proyecto tiene como objetivo servir de laboratorio personal para practicar, documentar y asentar las bases de las características fundamentales de **Kotlin**, aplicando principios de **Arquitectura Limpia** y una **estrategia de testing robusta**.

## 🚀 Objetivos del Proyecto
- [x] **Sintaxis Básica**: Variables, tipos de datos, control de flujo.
- [x] **Funciones**: Funciones de orden superior, lambdas, extensiones.
- [x] **Programación Orientada a Objetos**: Clases, herencia, interfaces, data classes, sealed classes.
- [x] **Colecciones y Genéricos**: Listas, mapas, sets y el uso de tipos genéricos.
- [x] **Null Safety**: Manejo de nulos, operador elvis, safe calls.
- [ ] **Corrutinas**: Programación asíncrona y concurrencia.
- [x] **Ecosistema**: Uso de Gradle (Kotlin DSL) y Version Catalogs.
- [x] **Persistencia de Datos**: Implementación de una base de datos SQLite con Arquitectura Limpia.
- [x] **Testing**: Cobertura de tests unitarios y de integración para las capas de Dominio y Datos.
- [x] **Inyección de Dependencias**: Gestión de dependencias con Koin.

## 🛠️ Tecnologías
- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **JDK:** 21
- **Sistema de Construcción:** Gradle (Kotlin DSL)
- **Gestión de Dependencias:** Version Catalog (`libs.versions.toml`)
- **Inyección de Dependencias:** [Koin](https://insert-koin.io/)
- **Base de Datos:** SQLite (mediante `org.xerial:sqlite-jdbc`)
- **Testing:** JUnit 5, MockK

## 🔍 Herramientas de Calidad de Código
- **Detekt:** Una herramienta de análisis estático para Kotlin que ayuda a mantener la calidad del código, detectar "code smells" y asegurar el cumplimiento de estándares de codificación.
  - **Configuración:** Las reglas de Detekt se configuran en `config/detekt/detekt.yml`.
  - **Ejecución:** Se integra con el ciclo de vida de Gradle y se ejecuta automáticamente con la tarea `check`. También puedes ejecutarlo manualmente con `./gradlew detekt`.

## 📂 Estructura del Proyecto
El proyecto sigue una estructura de Arquitectura Limpia, organizada en capas:
- `src/main/kotlin/domain`: Contiene los modelos, interfaces de repositorio y casos de uso (lógica de negocio pura).
- `src/main/kotlin/data`: Contiene las implementaciones de repositorio y el gestor de base de datos (detalles de persistencia).
- `src/main/kotlin/presentation`: Contiene el punto de entrada de la aplicación y la documentación.
- `src/main/resources`: Contiene scripts SQL para la inicialización de la base de datos.
- `gradle/libs.versions.toml`: Centralización de versiones y librerías.

## 📚 Documentación Detallada
Para una comprensión más profunda de aspectos específicos del proyecto:
- **Implementación de SQLite y Arquitectura Limpia**: Consulta [`src/main/kotlin/presentation/documentation/SQLiteReadme.md`](src/main/kotlin/presentation/documentation/SQLiteReadme.md)
- **Estrategia de Testing**: Consulta [`src/main/kotlin/presentation/documentation/TestingReadme.md`](src/main/kotlin/presentation/documentation/TestingReadme.md)

---
Creado para practicar y mejorar el dominio del ecosistema Kotlin.