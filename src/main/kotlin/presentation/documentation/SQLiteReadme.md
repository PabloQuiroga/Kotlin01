# Implementación de SQLite con Arquitectura Limpia en Kotlin

Este documento detalla la implementación de una base de datos SQLite en el proyecto Kotlin, siguiendo los principios de la Arquitectura Limpia (Clean Architecture).

## 🚀 Objetivo

El objetivo principal fue integrar una solución de persistencia de datos (SQLite) de manera organizada y desacoplada, permitiendo que la lógica de negocio (Dominio) sea independiente de los detalles de la base de datos (Datos).

## 📂 Estructura de Directorios Clave

La implementación se distribuye en las siguientes capas y directorios:

*   **`src/main/kotlin/domain/model`**:
    *   `User.kt`, `Address.kt`, `Geo.kt`, `Product.kt`, `Category.kt`: Modelos de datos principales de la aplicación. Han sido actualizados para incluir IDs (`Long?`) para su persistencia en la base de datos.
*   **`src/main/kotlin/domain/repository`**:
    *   `UserRepository.kt`, `CategoryRepository.kt`, `ProductRepository.kt`: Interfaces que definen el contrato para las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los modelos `User`, `Category` y `Product` respectivamente. Estas interfaces son agnósticas a la tecnología de persistencia.
*   **`src/main/kotlin/domain/usecase`**:
    *   `UserUseCases.kt`, `CategoryUseCases.kt`, `ProductUseCases.kt`: Clases que encapsulan la lógica de negocio relacionada con los usuarios, categorías y productos. Utilizan las interfaces de repositorio correspondientes para realizar operaciones de datos sin conocer los detalles de implementación.
*   **`src/main/kotlin/data/source`**:
    *   `DatabaseManager.kt`: Objeto singleton encargado de gestionar la conexión a la base de datos SQLite, la creación del esquema (tablas `geos`, `addresses`, `users`, `categories`, `products`) y la carga de scripts SQL externos.
*   **`src/main/kotlin/data/repository`**:
    *   `UserRepositoryImpl.kt`, `CategoryRepositoryImpl.kt`, `ProductRepositoryImpl.kt`: Implementaciones concretas de las interfaces de repositorio. Contienen la lógica JDBC para interactuar con SQLite, incluyendo transacciones y mapeo de `ResultSet` a objetos de dominio.
*   **`src/main/kotlin/presentation/documentation`**:
    *   Este archivo `SQLiteReadme.md` y `TestingReadme.md`.
*   **`src/main/kotlin/presentation/Main.kt`**:
    *   Punto de entrada de la aplicación. Contiene la clase `AppRunner` que orquesta la inicialización de la base de datos, la carga de datos iniciales y la demostración de las operaciones CRUD para `User`, `Category` y `Product` a través de los casos de uso.
*   **`src/main/resources/initial_data.sql`**:
    *   Archivo SQL que contiene sentencias `INSERT` para poblar la base de datos con datos de ejemplo para `User`, `Category` y `Product` al inicio, si la base de datos está vacía.

## 🛠️ Dependencias

La integración con SQLite se realiza a través del driver JDBC:

*   **`org.xerial:sqlite-jdbc`**: Añadido en `gradle/libs.versions.toml` y `build.gradle.kts`.

## 🚀 Funcionalidades Implementadas

*   **Conexión a SQLite**: Gestión de la conexión a una base de datos SQLite basada en archivo (`my_clean_architecture_database.db`).
*   **Creación de Tablas**: `DatabaseManager` crea automáticamente las tablas `geos`, `addresses`, `users`, `categories` y `products` con sus respectivas relaciones y claves foráneas al iniciar la aplicación.
*   **Operaciones CRUD para `User`**: Implementación completa de Crear, Leer (todos y por ID), Actualizar y Eliminar usuarios, incluyendo sus `Address` y `Geo` asociados, manejando transacciones para asegurar la integridad.
*   **Operaciones CRUD para `Category`**: Implementación completa de Crear, Leer (todos y por ID), Actualizar y Eliminar categorías.
*   **Operaciones CRUD para `Product`**: Implementación completa de Crear, Leer (todos y por ID), Actualizar y Eliminar productos, incluyendo su `Category` asociada.
*   **Carga de Datos Iniciales**: La aplicación verifica si la base de datos está vacía y, si es así, ejecuta las sentencias SQL del archivo `src/main/resources/initial_data.sql` para precargar datos para usuarios, categorías y productos.
*   **Arquitectura Limpia**: Separación clara de responsabilidades entre las capas de Dominio, Datos y Presentación, facilitando la mantenibilidad y la escalabilidad.
*   **Inyección de Dependencias Manual**: Las dependencias (repositorios, casos de uso) se gestionan y se inyectan manualmente en la clase `AppRunner`, demostrando un control explícito sobre el flujo de dependencias.

## ✅ Estrategia de Testing

El proyecto cuenta con una estrategia de testing robusta que cubre las capas de Dominio y Datos. Se han implementado tests unitarios para los Casos de Uso (capa de Dominio) utilizando MockK, y tests de integración para las implementaciones de Repositorio y el `DatabaseManager` (capa de Datos) utilizando una base de datos SQLite en memoria.

Para un detalle exhaustivo sobre la estrategia de testing, las herramientas utilizadas y la cobertura, consulta el archivo [`TestingReadme.md`](TestingReadme.md).

## 💡 Cómo Ejecutar la Aplicación

Para ver la implementación en acción:

1.  **Sincronizar Proyecto Gradle**: Asegúrate de que tu IDE (Android Studio/IntelliJ IDEA) haya sincronizado el proyecto con los archivos Gradle para descargar las dependencias. Esto se hace automáticamente o manualmente desde `File > Sync Project with Gradle Files`.
2.  **Ejecutar `Main.kt`**:
    *   Abre el archivo `src/main/kotlin/presentation/Main.kt`.
    *   Haz clic en el icono de "play" (triángulo verde) junto a la función `main` o la clase `AppRunner` y selecciona "Run 'AppRunnerKt'".

Al ejecutar, verás la salida en la consola que muestra la inicialización de la base de datos, la carga de datos iniciales (si aplica) y las operaciones CRUD de demostración para `User`, `Category` y `Product`. Se creará un archivo `my_clean_architecture_database.db` en la raíz de tu proyecto.

## 🔮 Futuras Consideraciones

*   **Migraciones de Base de Datos**: Para gestionar la evolución del esquema de la base de datos en el futuro, se recomienda integrar una herramienta de migración como [Flyway](https://flywaydb.org/) o [Liquibase](https://www.liquibase.org/). Esto permitiría versionar los cambios del esquema de forma controlada.
*   **Manejo de Errores**: Implementar un manejo de errores más robusto y específico para la aplicación.
*   **Librería de Inyección de Dependencias**: Para aplicaciones más grandes o con una complejidad de dependencias creciente, se podría considerar la integración de una librería de inyección de dependencias (como Koin o Dagger) para automatizar y simplificar la gestión de dependencias.