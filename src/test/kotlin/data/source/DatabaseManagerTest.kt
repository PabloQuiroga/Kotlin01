package data.source

import data.repository.CategoryRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.UserRepositoryImpl
import di.allModules
import domain.model.Category
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseManagerTest : KoinTest { // Extender de KoinTest para usar inject/get

    private lateinit var connection: Connection

    // Inyectar los repositorios usando Koin
    private val userRepository: UserRepositoryImpl by inject()
    private val categoryRepository: CategoryRepositoryImpl by inject()
    private val productRepository: ProductRepositoryImpl by inject()

    // Necesitamos una categoría de prueba para los productos en algunos tests
    private lateinit var testCategory: Category

    @BeforeEach
    fun setUp() {
        // Iniciar Koin con los módulos de la aplicación
        startKoin {
            modules(allModules)
        }

        // Usar una base de datos SQLite en memoria para cada test
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        DatabaseManager.setTestConnection(connection) // Inyectar la conexión de prueba
        DatabaseManager.initDatabase() // Inicializar el esquema y cargar datos iniciales en la DB en memoria
        DatabaseManager.loadInitialData()
        // Añadir una categoría de prueba para que los productos puedan referenciarla
        // Cambiamos el nombre para que no haya conflicto con initial_data.sql
        testCategory = categoryRepository.addCategory(
            Category(
                name = "Test Category",
                description = "Category for testing purposes"
            )
        )
    }

    @AfterEach
    fun tearDown() {
        connection.close()
        DatabaseManager.clearTestConnection() // Limpiar la conexión de test
        stopKoin() // Detener Koin después de cada test
    }

    @Test
    fun `initDatabase should create all necessary tables`() {
        // initDatabase ya se llama en setUp, solo verificamos
        val stmt = connection.createStatement()
        val rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';")

        val tables = mutableListOf<String>()
        while (rs.next()) {
            tables.add(rs.getString("name"))
        }

        assertTrue(tables.contains("geos"))
        assertTrue(tables.contains("addresses"))
        assertTrue(tables.contains("users"))
        assertTrue(tables.contains("categories"))
        assertTrue(tables.contains("products"))

        rs.close()
        stmt.close()
    }

    @Test
    fun `initial data should be loaded correctly after initDatabase`() { // Renombré el test para mayor claridad
        // Los datos iniciales ya se cargaron en setUp() a través de initDatabase()
        // Solo necesitamos verificar que estén presentes.

        // Verificar que los datos se cargaron correctamente usando los repositorios
        val users = userRepository.getAllUsers()
        val categories = categoryRepository.getAllCategories()
        val products = productRepository.getAllProducts()

        // initial_data.sql tiene 3 usuarios, 3 categorías y 5 productos
        // En setUp se añade 1 categoría ("Test Category")
        // Por lo tanto, esperamos 3 usuarios, 1 categoría (de setUp) + 3 de initial_data.sql = 4 categorías.
        // Y 5 productos.

        assertEquals(3, users.size) // 3 de initial_data.sql
        assertEquals(4, categories.size) // 1 de setUp + 3 de initial_data.sql
        assertEquals(5, products.size) // Los 5 de initial_data.sql

        assertTrue(users.any { it.username == "alices" })
        assertTrue(categories.any { it.name == "Test Category" }) // De setUp
        assertTrue(categories.any { it.name == "Electronics" }) // De initial_data.sql
        assertTrue(categories.any { it.name == "Books" }) // De initial_data.sql
        assertTrue(products.any { it.name == "Laptop X1" })
    }

    @Test
    fun `loadSqlFile should rollback all changes if an SQLException occurs during execution`() {
        // 1. Obtener el estado de la base de datos después de la carga inicial exitosa en setUp
        val initialUserCount = userRepository.getAllUsers().size
        val initialCategoryCount = categoryRepository.getAllCategories().size
        val initialProductCount = productRepository.getAllProducts().size

        // 2. Intentar cargar initial_data.sql de nuevo.
        //    Esto causará UNIQUE constraint violations para usuarios, categorías y productos,
        //    lo que debería disparar una SQLException y el mecanismo de rollback de loadSqlFile.
        assertThrows(SQLException::class.java) {
            DatabaseManager.loadSqlFile("initial_data.sql")
        }

        // 3. Verificar que el estado de la base de datos no ha cambiado
        //    Esto significa que no se añadieron nuevos datos ni se modificaron los existentes
        //    debido al rollback.
        assertEquals(initialUserCount, userRepository.getAllUsers().size)
        assertEquals(initialCategoryCount, categoryRepository.getAllCategories().size)
        assertEquals(initialProductCount, productRepository.getAllProducts().size)
    }
}