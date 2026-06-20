package presentation

import data.source.DatabaseManager
import java.sql.SQLException

fun main() {
    initializeDatabase()
}

private fun initializeDatabase() {
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
