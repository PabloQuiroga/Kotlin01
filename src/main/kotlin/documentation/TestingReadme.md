# Estrategia de Testing en el Proyecto Kotlin (Clean Architecture)

Este documento detalla la estrategia y la implementación de tests unitarios y de integración para las capas de Dominio (`domain`) y Datos (`data`) de este proyecto Kotlin, siguiendo los principios de la Arquitectura Limpia.

## 🚀 Objetivo del Testing

El objetivo principal es asegurar la corrección y robustez de la lógica de negocio y la interacción con la base de datos, manteniendo un alto grado de aislamiento entre las capas para facilitar el desarrollo, la depuración y el mantenimiento.

## 🛠️ Herramientas Utilizadas

*   **JUnit 5**: Framework de testing principal para Kotlin/JVM.
*   **MockK**: Librería de mocking para Kotlin, utilizada para crear dobles de prueba (mocks) de interfaces y clases, permitiendo el aislamiento de unidades de código.
*   **Koin**: Framework de Inyección de Dependencias utilizado para gestionar y proporcionar las instancias de las dependencias en los tests de integración, asegurando que se prueben los componentes con sus dependencias reales (o configuradas por Koin).
*   **SQLite en Memoria**: Para los tests de la capa de datos, se utiliza una base de datos SQLite que se ejecuta completamente en memoria (`jdbc:sqlite::memory:`), lo que garantiza tests rápidos, aislados y sin efectos secundarios en el sistema de archivos.

## 📂 Cobertura de Tests por Capa

### 1. Testing de la Capa de Dominio (`domain`)

La capa de Dominio contiene la lógica de negocio central y las entidades. Los tests en esta capa se centran en verificar el comportamiento de los Casos de Uso, asegurando que la lógica de negocio se ejecuta correctamente y que la interacción con las interfaces de repositorio es la esperada.

*   **Qué se testea**:
    *   **Casos de Uso (`*UseCases`)**: Se verifica que los métodos de los casos de uso invocan correctamente los métodos correspondientes de las interfaces de repositorio. También se testearía cualquier lógica de negocio adicional (validaciones, transformaciones) si estuviera presente directamente en el caso de uso.
    *   **Modelos (Entidades)**: Al ser `data class` simples, su comportamiento básico (equals, hashCode, toString, copy) está garantizado por Kotlin. No se requieren tests explícitos a menos que contengan lógica compleja.
    *   **Interfaces de Repositorio**: No se testean directamente, ya que son solo contratos.

*   **Cómo se testea**:
    *   **Tests Unitarios**: Se utilizan mocks de las interfaces de repositorio (`UserRepository`, `CategoryRepository`, `ProductRepository`) para aislar completamente los casos de uso de la implementación real de la base de datos.
    *   **MockK**: Permite definir el comportamiento esperado de los mocks (`every { ... } returns ...`) y verificar que los métodos fueron llamados con los argumentos correctos (`verify { ... }`).
    *   **Koin**: **No se utiliza Koin en estos tests unitarios**, ya que el objetivo es probar la unidad de código de forma aislada con mocks.

*   **Archivos de Test Implementados**:
    *   `src/test/kotlin/domain/usecase/UserUseCasesTest.kt`
    *   `src/test/kotlin/domain/usecase/CategoryUseCasesTest.kt`
    *   `src/test/kotlin/domain/usecase/ProductUseCasesTest.kt`

### 2. Testing de la Capa de Datos (`data`)

La capa de Datos es responsable de la persistencia y la interacción con la base de datos. Los tests en esta capa aseguran que las operaciones CRUD se ejecutan correctamente contra una base de datos real (en memoria para tests) y que el mapeo de datos es preciso.

*   **Qué se testea**:
    *   **Implementaciones de Repositorio (`*RepositoryImpl`)**: Se verifica que las sentencias SQL son correctas, que los datos se insertan, leen, actualizan y eliminan correctamente, y que el mapeo entre `ResultSet` y los modelos de dominio funciona bidireccionalmente.
    *   **`DatabaseManager`**: Se testea la correcta inicialización del esquema de la base de datos (`initDatabase()`) y la capacidad de cargar y ejecutar scripts SQL desde archivos de recursos (`loadSqlFile()`), incluyendo el manejo transaccional y el rollback en caso de error.

*   **Cómo se testea**:
    *   **Tests de Integración**: Se utiliza una base de datos SQLite en memoria (`jdbc:sqlite::memory:`) para cada test o suite de tests. Esto proporciona un entorno de base de datos real pero aislado y rápido.
    *   **`DatabaseManager` modificado para testabilidad**: Se añadió un mecanismo (`setTestConnection`, `clearTestConnection`) a `DatabaseManager` para permitir la inyección de la conexión de base de datos en memoria desde los tests.
    *   **Koin**: Se integra Koin para gestionar las dependencias de los repositorios.
        *   Las clases de test extienden `KoinTest`.
        *   Se inicia el contexto de Koin con `startKoin { modules(allModules) }` en el método `@BeforeEach`.
        *   Las instancias de los repositorios se obtienen mediante inyección de Koin (`val repository: MyRepository by inject()`).
        *   El contexto de Koin se detiene con `stopKoin()` en el método `@AfterEach` para asegurar el aislamiento entre tests.

*   **Archivos de Test Implementados**:
    *   `src/test/kotlin/data/repository/UserRepositoryImplTest.kt`
    *   `src/test/kotlin/data/repository/CategoryRepositoryImplTest.kt`
    *   `src/test/kotlin/data/repository/ProductRepositoryImplTest.kt`
    *   `src/test/kotlin/data/source/DatabaseManagerTest.kt`

## 💡 Cómo Ejecutar los Tests

Para ejecutar todos los tests del proyecto:

1.  **Sincronizar Proyecto Gradle**: Asegúrate de que tu IDE (Android Studio/IntelliJ IDEA) haya sincronizado el proyecto con los archivos Gradle para descargar las dependencias de testing (JUnit, MockK, Koin).
2.  **Ejecutar desde el IDE**:
    *   Puedes navegar a la carpeta `src/test/kotlin` en la vista de proyecto.
    *   Haz clic derecho en la carpeta `kotlin` (dentro de `src/test`) y selecciona "Run 'All Tests'".
    *   Alternativamente, puedes abrir cada archivo de test individualmente y hacer clic en el icono de "play" verde junto a la clase de test o a métodos de test específicos.

## 🔮 Futuras Consideraciones para Testing

*   **Tests de Integración de Capa de Presentación**: Si se desarrollara una capa de presentación más compleja (ej. una API REST), se añadirían tests para verificar la integración de los casos de uso con los controladores/endpoints.
*   **Tests End-to-End**: Para aplicaciones completas, se implementarían tests que cubran el flujo completo de la aplicación, desde la interfaz de usuario hasta la base de datos.
*   **Tests de Rendimiento**: Para componentes críticos, se podrían añadir tests de rendimiento para medir y optimizar el tiempo de respuesta.
*   **Cobertura de Código**: Utilizar herramientas de cobertura de código (como JaCoCo) para medir qué porcentaje del código está cubierto por tests y identificar áreas que necesitan más atención.