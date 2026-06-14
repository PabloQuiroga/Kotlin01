package data.source

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseManager {
    private const val DB_URL = "jdbc:sqlite:./my_clean_architecture_database.db"

    fun getConnection(): Connection {
        try {
            Class.forName("org.sqlite.JDBC") // Cargar el driver JDBC de SQLite
            return DriverManager.getConnection(DB_URL)
        } catch (e: ClassNotFoundException) {
            throw SQLException("SQLite JDBC driver not found.", e)
        } catch (e: SQLException) {
            throw SQLException("Error connecting to the database: ${e.message}", e)
        }
    }

    fun initDatabase() {
        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                // Enable foreign key support for SQLite
                stmt.execute("PRAGMA foreign_keys = ON;")

                // Create Geo table
                val createGeoTableSql = """
                    CREATE TABLE IF NOT EXISTS geos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        lat TEXT NOT NULL,
                        lng TEXT NOT NULL
                    );
                """.trimIndent()
                stmt.execute(createGeoTableSql)
                println("Table 'geos' created or already exists.")

                // Create Address table
                val createAddressTableSql = """
                    CREATE TABLE IF NOT EXISTS addresses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        street TEXT NOT NULL,
                        suite TEXT NOT NULL,
                        city TEXT NOT NULL,
                        zipcode TEXT NOT NULL,
                        geo_id INTEGER,
                        FOREIGN KEY (geo_id) REFERENCES geos(id) ON DELETE CASCADE
                    );
                """.trimIndent()
                stmt.execute(createAddressTableSql)
                println("Table 'addresses' created or already exists.")

                // Create User table
                val createUserTableSql = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        username TEXT NOT NULL UNIQUE,
                        email TEXT NOT NULL UNIQUE,
                        phone TEXT NOT NULL,
                        website TEXT NOT NULL,
                        address_id INTEGER,
                        FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE CASCADE
                    );
                """.trimIndent()
                stmt.execute(createUserTableSql)
                println("Table 'users' created or already exists.")

                // --- NUEVAS TABLAS PARA CATEGORY Y PRODUCT ---

                // Create Category table
                val createCategoryTableSql = """
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT NOT NULL
                    );
                """.trimIndent()
                stmt.execute(createCategoryTableSql)
                println("Table 'categories' created or already exists.")

                // Create Product table
                val createProductTableSql = """
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        price REAL NOT NULL,
                        category_id INTEGER,
                        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                    );
                """.trimIndent()
                stmt.execute(createProductTableSql)
                println("Table 'products' created or already exists.")
            }
        }
    }

    fun loadSqlFile(fileName: String) {
        val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("Resource file not found: $fileName")

        val sqlStatements = resourceStream.bufferedReader().use { it.readText() }
            .split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        var conn: Connection? = null
        try {
            conn = getConnection()
            conn.autoCommit = false // Start transaction for loading data
            conn.createStatement().use { stmt ->
                sqlStatements.forEach { sql ->
                    if (sql.isNotBlank()) { // Ensure we don't try to execute empty strings
                        try {
                            stmt.execute(sql)
                        } catch (e: SQLException) {
                            System.err.println("Error executing SQL statement: $sql - ${e.message}")
                            throw e // Re-throw to rollback transaction
                        }
                    }
                }
            }
            conn.commit() // Commit transaction
            println("SQL file '$fileName' loaded successfully.")
        } catch (e: SQLException) {
            System.err.println("Error loading SQL file '$fileName': ${e.message}")
            conn?.rollback() // Rollback on error if connection exists
            throw e
        } finally {
            conn?.autoCommit = true // Restore auto-commit
            conn?.close() // Ensure connection is closed
        }
    }
}