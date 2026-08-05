package basics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * Fundamentos, Val vs Var y Null Safety
 */
class NullSafetyTest {

    @Test
    fun `test var`() {
        // var: Mutable.
        var score = 10
        score = 15
        assertEquals(15, score)
    }

    @Disabled("Error de compilación")
    @Test
    fun test_val() {
        // val: Inmutable (read-only). Preferible para arquitectura robusta.
        val age = 30
        //age = 31 // Error de compilación
    }

    @Test
    fun test_stringTemplates() {
        val name = "pablo"
        val price = 12.34
        val tax = 2.1

        val greeting = "Hello, $name!" // Incorpora variable en la cadena
        val precioTotal = price + tax

        assertEquals("Hello, $name!", greeting)
        assertEquals("${price + tax}", precioTotal.toString()) // Expresiones dentro de la cadena
    }

    @Test
    fun `test null safety basics`(){
        // 1. Tipos no nulos por defecto
        var name: String = "Kotlin"
        // name = null // Esto ni siquiera compilaría.

        // 2. Tipos nulos (Nullable)
        var nullableName: String? = "Pablo"
        nullableName = null

        // 3. El operador Safe Call (?.)
        val length = nullableName?.length
        assertNull(length)

        // 4. El operador Elvis (?:) - Valor por defecto
        val finalLength = nullableName?.length ?: 0
        assertEquals(0, finalLength)

        // 5. El operador Double Bang (!!) - "Yo sé lo que hago" (Peligroso)
        // val forcedLength = nullableName!!.length // Lanzaría NullPointerException
    }

    @Test
    fun `test safety cast`(){
        // 6. El operador Safe Cast (as?)
        // Intenta castear a un tipo, devolviendo null si falla en lugar de lanzar una ClassCastException.

        val obj: Any = "Hola Kotlin"
        val str: String? = obj as? String // Casteo exitoso, str es "Hola Kotlin"

        val num: Any = 123
        val str2: String? = num as? String // Casteo fallido, str2 es null

        println("str: $str") // Imprime: str: Hola Kotlin
        println("str2: $str2") // Imprime: str2: null

        // Se puede combinar con el operador Elvis (?:) para proporcionar un valor por defecto
        val result = num as? String ?: "No es una cadena"
        println("result: $result") // Imprime: result: No es una cadena
    }
}
