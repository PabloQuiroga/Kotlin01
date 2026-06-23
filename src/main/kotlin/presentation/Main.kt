package presentation

import data.source.DatabaseManager
import di.allModules
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import presentation.categories.CategoryOperations
import presentation.products.ProductOperations
import presentation.users.UserOperations
import java.sql.SQLException

fun main() {
    startKoin { modules(allModules) } // Usar modules(allModules) para pasar la lista de módulos
    initializeDatabase()

    demoKoin()
}

private fun initializeDatabase() {
    // Llama a deleteDatabaseFile() si quieres eliminar la base de datos antes de inicializarla
    DatabaseManager.deleteDatabaseFile()

    println("Initializing database...")
    try {
        DatabaseManager.initDatabase()
        println("Database initialized successfully.")
    } catch (e: SQLException) {
        println("Error initializing database: ${e.message}")
        e.printStackTrace()
        // Si la base de datos no se puede inicializar, las operaciones posteriores fallarán.
        // Considerar una estrategia de salida o manejo de errores más robusta en una app real.
    }
}

private fun demoKoin(){
    // Cargar datos iniciales
    DatabaseManager.loadInitialData()

    // Ejemplo de uso de dependencias con Koin
    val categoryOperations: CategoryOperations by inject(CategoryOperations::class.java)
    val productOperations: ProductOperations by inject(ProductOperations::class.java)
    val userOperations: UserOperations by inject(UserOperations::class.java)

    // Obtener y mostrar todas las categorías
    val categories = categoryOperations.getAllCategories()
    println("Todas las categorías:")
    categories.forEach { println(it) }

    // Obtener y mostrar todos los productos
    val products = productOperations.getAllProducts()
    println("Todos los productos:")
    products.forEach { println(it) }

    // Obtener y mostrar todas las categorías
    val users = userOperations.getAllUsers()
    println("Todos los usuarios:")
    users.forEach { println(it) }
}
