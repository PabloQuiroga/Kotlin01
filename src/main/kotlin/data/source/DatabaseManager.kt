package data.source

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseManager {
    private const val DB_URL = "jdbc:sqlite:./my_clean_architecture_database.db"

    // Usamos ThreadLocal para almacenar la conexión de prueba y el flag isTestMode
    // Esto asegura que cada hilo (cada test) tenga su propia instancia, evitando conflictos en ejecución paralela.
    private val threadLocalTestConnection = ThreadLocal<Connection>()
    private val threadLocalIsTestMode = ThreadLocal<Boolean>()

    // Método para establecer una conexión de prueba para el hilo actual
    fun setTestConnection(conn: Connection) {
        threadLocalTestConnection.set(conn)
        threadLocalIsTestMode.set(true) // Activar modo de prueba para el hilo actual
    }

    // Método para limpiar la conexión de prueba para el hilo actual
    fun clearTestConnection() {
        threadLocalTestConnection.remove() // Limpiar la conexión de prueba del hilo actual
        threadLocalIsTestMode.remove() // Desactivar modo de prueba para el hilo actual
    }

    fun getConnection(): Connection {
        // Si hay una conexión de prueba para el hilo actual, la devolvemos
        val testConn = threadLocalTestConnection.get()
        if (testConn != null) {
            return testConn
        }

        // Si no, creamos una conexión normal
        try {
            Class.forName("org.sqlite.JDBC") // Cargar el driver JDBC de SQLite
            return DriverManager.getConnection(DB_URL)
        } catch (e: ClassNotFoundException) {
            throw SQLException("SQLite JDBC driver not found.", e)
        } catch (e: SQLException) {
            throw SQLException("Error connecting to the database: ${e.message}", e)
        }
    }

    // Nuevo método para ejecutar un bloque con una conexión, manejando el cierre condicionalmente
    fun <T> executeWithConnection(block: (Connection) -> T): T {
        val conn = getConnection()
        // Obtenemos el estado de isTestMode para el hilo actual
        val isTestMode = threadLocalIsTestMode.get() ?: false
        try {
            return block(conn)
        } finally {
            // Solo cerramos la conexión si NO estamos en modo de prueba para este hilo
            // La conexión de prueba será cerrada por el framework de testing (ej. @AfterEach)
            if (!isTestMode) {
                conn.close()
            } else {
                println("[DEBUG] Thread ${Thread.currentThread().name}: executeWithConnection finally block: NOT closing connection because isTestMode is true.")
            }
        }
    }

    fun initDatabase() {
        loadSqlFile("sql/schema.sql")
        println("Database schema initialized from sql/schema.sql.")
    }

    fun loadSqlFile(fileName: String) {
        val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("Resource file not found: $fileName")

        val sqlStatements = resourceStream.bufferedReader().use { it.readText() }
            .split(";")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        executeWithConnection { conn ->
            // Usamos el flag isTestMode para el hilo actual para determinar el entorno
            val isTestMode = threadLocalIsTestMode.get() ?: false

            if (isTestMode) {
                // Para el entorno de prueba, simplemente ejecutar sentencias sin gestión explícita de transacciones
                // La DB en memoria es nueva para cada test, no necesitamos commit/rollback dentro de loadSqlFile
                conn.createStatement().use { stmt ->
                    sqlStatements.forEach { sql ->
                        if (sql.isNotBlank()) {
                            try {
                                stmt.execute(sql)
                            } catch (e: SQLException) {
                                System.err.println("Error executing SQL statement in test env: $sql - ${e.message}")
                                throw e
                            }
                        }
                    }
                }
            } else {
                // Gestión de transacciones original para entornos que no son de prueba
                val initialAutoCommit = conn.autoCommit
                try {
                    conn.autoCommit = false // Iniciar transacción

                    conn.createStatement().use { stmt ->
                        sqlStatements.forEach { sql ->
                            if (sql.isNotBlank()) {
                                try {
                                    stmt.execute(sql)
                                } catch (e: SQLException) {
                                    System.err.println("Error executing SQL statement: $sql - ${e.message}")
                                    throw e // Relanzar para activar el rollback
                                }
                            }
                        }
                    }
                    conn.commit() // Confirmar transacción
                    println("SQL file '$fileName' loaded successfully.")
                } catch (e: SQLException) {
                    System.err.println("Error loading SQL file '$fileName': ${e.message}")
                    conn.rollback() // Revertir en caso de error
                    throw e
                } finally {
                    conn.autoCommit = initialAutoCommit // Restaurar el estado original de autoCommit
                }
            }
        }
    }
}