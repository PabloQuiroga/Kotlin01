import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ReadmeTest {

    @Test
    fun `README referencia documentación existente`() {
        listOf(
            "src/main/kotlin/documentation/BasicsReadme.md",
            "src/main/kotlin/documentation/SQLiteReadme.md",
            "src/main/kotlin/documentation/TestingReadme.md"
        ).forEach { path ->
            assertTrue(Files.exists(Path.of(path)), "No existe el archivo: $path")
        }
    }
}
