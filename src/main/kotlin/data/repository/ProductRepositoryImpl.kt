package data.repository

import data.source.DatabaseManager
import domain.model.Category
import domain.model.Product
import domain.repository.ProductRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class ProductRepositoryImpl : ProductRepository {

    // Modificado para usar DatabaseManager.executeWithConnection
    private fun <T> withConnection(block: (Connection) -> T): T {
        return DatabaseManager.executeWithConnection(block)
    }

    override fun getAllProducts(): List<Product> = withConnection { conn ->
        val products = mutableListOf<Product>()
        val sql = """
            SELECT
                p.id AS product_id, p.name AS product_name, p.price,
                c.id AS category_id, c.name AS category_name, c.description
            FROM products p
            JOIN categories c ON p.category_id = c.id
        """.trimIndent()
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs))
            }
        }
        products
    }

    override fun getProductById(id: Long): Product? = withConnection { conn ->
        val sql = """
            SELECT
                p.id AS product_id, p.name AS product_name, p.price,
                c.id AS category_id, c.name AS category_name, c.description
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.id = ?
        """.trimIndent()
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, id)
            val rs = pstmt.executeQuery()
            if (rs.next()) {
                mapResultSetToProduct(rs)
            } else {
                null
            }
        }
    }

    override fun addProduct(product: Product): Product = withConnection { conn ->
        val sql = "INSERT INTO products(name, price, category_id) VALUES(?, ?, ?)"
        conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setString(1, product.name)
            pstmt.setDouble(2, product.price)
            pstmt.setLong(3, product.category.id ?: throw SQLException("Category ID cannot be null for adding a product."))
            pstmt.executeUpdate()

            val generatedKeys = pstmt.generatedKeys
            if (generatedKeys.next()) {
                val newId = generatedKeys.getLong(1)
                return@withConnection product.copy(id = newId)
            } else {
                throw SQLException("Failed to retrieve auto-generated product ID.")
            }
        }
    }

    override fun updateProduct(product: Product): Product? = withConnection { conn ->
        val sql = "UPDATE products SET name = ?, price = ?, category_id = ? WHERE id = ?"
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, product.name)
            pstmt.setDouble(2, product.price)
            pstmt.setLong(3, product.category.id ?: throw SQLException("Category ID cannot be null for updating a product."))
            pstmt.setLong(4, product.id ?: throw SQLException("Product ID cannot be null for update."))
            val rowsAffected = pstmt.executeUpdate()
            if (rowsAffected > 0) {
                return@withConnection product
            } else {
                return@withConnection null
            }
        }
    }

    override fun deleteProduct(id: Long): Boolean = withConnection { conn ->
        val sql = "DELETE FROM products WHERE id = ?"
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, id)
            val rowsAffected = pstmt.executeUpdate()
            return@withConnection rowsAffected > 0
        }
    }

    private fun mapResultSetToProduct(rs: ResultSet): Product {
        val category = Category(
            id = rs.getLong("category_id"),
            name = rs.getString("category_name"),
            description = rs.getString("description")
        )
        return Product(
            id = rs.getLong("product_id"),
            name = rs.getString("product_name"),
            price = rs.getDouble("price"),
            category = category
        )
    }
}