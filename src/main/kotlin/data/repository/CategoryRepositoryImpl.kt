package data.repository

import data.source.DatabaseManager
import domain.model.Category
import domain.repository.CategoryRepository
import util.SqlLoader
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class CategoryRepositoryImpl : CategoryRepository {

    private val categorySqlQueries: Map<String, String> =
        SqlLoader.loadSqlQueries("sql/category.sql")

    // Reemplazamos la función withConnection local por la de DatabaseManager
    private fun <T> withConnection(block: (Connection) -> T): T {
        return DatabaseManager.executeWithConnection(block)
    }

    override fun getAllCategories(): List<Category> = withConnection { conn ->
        val categories = mutableListOf<Category>()
        val sql = categorySqlQueries["getAllCategories"]
            ?: throw IllegalStateException("SQL query 'getAllCategories' not found.")
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs))
            }
        }
        categories
    }

    override fun getCategoryById(id: Long): Category? = withConnection { conn ->
        val sql = categorySqlQueries["getCategoryById"]
            ?: throw IllegalStateException("SQL query 'getCategoryById' not found.")
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, id)
            val rs = pstmt.executeQuery()
            if (rs.next()) {
                mapResultSetToCategory(rs)
            } else {
                null
            }
        }
    }

    override fun addCategory(category: Category): Category = withConnection { conn ->
        val sql = categorySqlQueries["addCategory"]
            ?: throw IllegalStateException("SQL query 'addCategory' not found.")
        conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setString(1, category.name)
            pstmt.setString(2, category.description)
            pstmt.executeUpdate()

            val generatedKeys = pstmt.generatedKeys
            if (generatedKeys.next()) {
                val newId = generatedKeys.getLong(1)
                return@withConnection category.copy(id = newId)
            } else {
                throw SQLException("Failed to retrieve auto-generated category ID.")
            }
        }
    }

    override fun updateCategory(category: Category): Category? = withConnection { conn ->
        val sql = categorySqlQueries["updateCategory"]
            ?: throw IllegalStateException("SQL query 'updateCategory' not found.")
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, category.name)
            pstmt.setString(2, category.description)
            pstmt.setLong(3, category.id ?: throw SQLException("Category ID cannot be null for update."))
            val rowsAffected = pstmt.executeUpdate()
            if (rowsAffected > 0) {
                return@withConnection category
            } else {
                return@withConnection null
            }
        }
    }

    override fun deleteCategory(id: Long): Boolean = withConnection { conn ->
        val sql = categorySqlQueries["deleteCategory"]
            ?: throw IllegalStateException("SQL query 'deleteCategory' not found.")
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, id)
            val rowsAffected = pstmt.executeUpdate()
            return@withConnection rowsAffected > 0
        }
    }

    private fun mapResultSetToCategory(rs: ResultSet): Category {
        return Category(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            description = rs.getString("description")
        )
    }
}