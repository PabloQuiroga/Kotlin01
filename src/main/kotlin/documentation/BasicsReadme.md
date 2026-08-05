# Fundamentos de Kotlin

Este documento detalla la implementación de tests unitarios a fin de ejemplificar las funcionalidades descritas.

El proyecto está organizado en módulos temáticos, cada uno con su propio archivo de pruebas en `app/src/test/kotlin/basics/`.

### 1. Null Safety y Fundamentos (`NullSafetyTest.kt`)
*   **Conceptos:** Inmutabilidad con `val` vs `var`.
*   **Null Safety:** Tipos nulos y no nulos, Operador Safe Call (`?.`), Operador Elvis (`?:`) y el peligro del Double Bang (`!!`). El compilador de Kotlin es el primer filtro de calidad, eliminando los `NullPointerException` desde el diseño.

### 2. Scope Functions (`ScopeFunctionsTest.kt`)
*   **Herramientas:** `let`, `run`, `with`, `apply`, `also`.
*   **Uso:** Diferencias entre referencias (`it` vs `this`) y valores de retorno.
 Usar la función adecuada mejora drásticamente la legibilidad y reduce el código "boilerplate" en la configuración de objetos.

### 3. Estructuras de Control (`ControlFlowTest.kt`)
*   **Expresiones:** Uso de `if` y `when` como expresiones que retornan valores.
*   **Potencia:** Rangos (`..`, `until`, `step`) y chequeo de tipos con `is` dentro de `when`.
 Preferir expresiones sobre sentencias para un código más funcional y menos propenso a errores de estado.

### 4. Programación Funcional (`FunctionalProgrammingTest.kt`)
*   **Fundamentos:** Lambdas, funciones de orden superior y tipos de funciones.
*   **Rendimiento:** El modificador `inline` para evitar la sobrecarga de memoria en lambdas.
 Las funciones son ciudadanos de primera clase, base fundamental de Jetpack Compose.

### 5. Extension Functions y Operadores (`ExtensionFunctionsTest.kt`)
*   **Extensibilidad:** Añadir funcionalidad a clases existentes (String, View, Int) sin usar herencia.
*   **Operator Overloading:** Sobrecarga de operadores como `+` (`plus`) para tipos personalizados.
 Las extensiones eliminan la necesidad de clases "Utils", integrando la lógica donde realmente pertenece.

### 6. Modelado de Datos (`DataModelingTest.kt`)
*   **Estructuras:** `data class` para inmutabilidad y `sealed class` para jerarquías de estado cerradas.
*   **Patrones:** Singletons con `object` y miembros de clase con `companion object`.
 Las `sealed classes` son el estándar de oro para representar estados de UI (Success, Error, Loading) en arquitecturas MVVM/MVI.

### 7. Colecciones y Transformaciones (`CollectionsTest.kt`)
*   **Operadores:** `map`, `filter`, `flatMap`, `groupBy`, `fold`.
*   **Búsqueda:** `find`, `any`, `all`.
 Manipular datos de forma declarativa evita errores lógicos comunes en bucles manuales.

### 8. Corrutinas (Asincronía) (`CoroutinesBasicsTest.kt`)
*   **Suspensión:** `suspend functions` y el concepto de hilos ligeros.
*   **Contexto:** Manejo de `Dispatchers` (Main, IO, Default).
*   **Testing:** Uso de `runTest` para pruebas asíncronas deterministas.
 Al usar `runTest`, el tiempo es virtual. Debes usar `testScheduler.currentTime` en lugar de `System.currentTimeMillis()` para validar retardos de forma precisa.

### 9. Flow (Programación Reactiva) (`FlowTest.kt`)
*   **Flujos:** Cold Flows vs Hot Flows (`StateFlow`, `SharedFlow`).
*   **Reactividad:** Operadores de flujo y recolección asíncrona.
 `StateFlow` es el reemplazo moderno de `LiveData`. En tests de `SharedFlow`, usa `yield()` para asegurar que los suscriptores estén listos antes de emitir, y `backgroundScope` para una gestión limpia de corrutinas.

### 10. Generics y Varianza (`GenericsTest.kt`)
*   **Reutilización:** Clases y funciones genéricas con restricciones (`Constraints`).
*   **Avanzado:** Conceptos de Covarianza (`out`) y Contravarianza (`in`).
 Entender la varianza es clave para diseñar librerías y componentes de arquitectura altamente reutilizables.
