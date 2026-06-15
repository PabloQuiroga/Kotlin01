package data.repository

import data.source.DatabaseManager
import domain.model.Category
import domain.model.Product
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager

class ProductRepositoryImplTest {

    private lateinit var connection: Connection
    private lateinit var productRepository: ProductRepositoryImpl
    private lateinit var categoryRepository: CategoryRepositoryImpl // Necesitamos Category para los productos

    private lateinit var testCategory: Category

    @BeforeEach
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        DatabaseManager.setTestConnection(connection)
        DatabaseManager.initDatabase() // Inicializar el esquema en la DB en memoria

        categoryRepository = CategoryRepositoryImpl()
        productRepository = ProductRepositoryImpl()

        // Añadir una categoría de prueba para que los productos puedan referenciarla
        testCategory = categoryRepository.addCategory(Category(name = "Electronics", description = "Devices"))
    }

    @AfterEach
    fun tearDown() {
        connection.close()
        DatabaseManager.clearTestConnection()
    }

    @Test
    fun `addProduct should insert a product and return the product with generated ID`() {
        val newProduct = Product(name = "Laptop X1", price = 1200.0, category = testCategory)
        val addedProduct = productRepository.addProduct(newProduct)

        assertNotNull(addedProduct.id)
        assertEquals(newProduct.name, addedProduct.name)
        assertEquals(newProduct.price, addedProduct.price)
        assertEquals(testCategory.id, addedProduct.category.id)

        val retrievedProduct = productRepository.getProductById(addedProduct.id!!)
        assertEquals(addedProduct, retrievedProduct)
    }

    @Test
    fun `getAllProducts should return all products in the database`() {
        val product1 = Product(name = "Smartphone Pro", price = 800.0, category = testCategory)
        productRepository.addProduct(product1)
        val product2 = Product(name = "Smartwatch", price = 250.0, category = testCategory)
        productRepository.addProduct(product2)

        val products = productRepository.getAllProducts()
        assertEquals(2, products.size)
        assertTrue(products.any { it.name == "Smartphone Pro" })
        assertTrue(products.any { it.name == "Smartwatch" })
    }

    @Test
    fun `getProductById should return the correct product when found`() {
        val newProduct = Product(name = "Tablet", price = 500.0, category = testCategory)
        val addedProduct = productRepository.addProduct(newProduct)

        val foundProduct = productRepository.getProductById(addedProduct.id!!)
        assertEquals(addedProduct, foundProduct)
    }

    @Test
    fun `getProductById should return null when product not found`() {
        val foundProduct = productRepository.getProductById(999L)
        assertNull(foundProduct)
    }

    @Test
    fun `updateProduct should update an existing product and return the updated product`() {
        val newProduct = Product(name = "Headphones", price = 150.0, category = testCategory)
        val addedProduct = productRepository.addProduct(newProduct)

        val updatedCategory = categoryRepository.addCategory(Category(name = "Audio", description = "Audio devices"))
        val updatedProduct = addedProduct.copy(name = "Updated Headphones", price = 120.0, category = updatedCategory)
        val result = productRepository.updateProduct(updatedProduct)

        assertNotNull(result)
        assertEquals("Updated Headphones", result?.name)
        assertEquals(120.0, result?.price)
        assertEquals(updatedCategory.id, result?.category?.id)

        val retrievedProduct = productRepository.getProductById(addedProduct.id!!)
        assertEquals(result, retrievedProduct)
    }

    @Test
    fun `updateProduct should return null if product to update does not exist`() {
        val nonExistentProduct = Product(id = 999L, name = "Non Existent", price = 0.0, category = testCategory)
        val result = productRepository.updateProduct(nonExistentProduct)
        assertNull(result)
    }

    @Test
    fun `deleteProduct should delete an existing product and return true`() {
        val newProduct = Product(name = "Webcam", price = 70.0, category = testCategory)
        val addedProduct = productRepository.addProduct(newProduct)

        val isDeleted = productRepository.deleteProduct(addedProduct.id!!)
        assertTrue(isDeleted)

        val foundProduct = productRepository.getProductById(addedProduct.id!!)
        assertNull(foundProduct)
    }

    @Test
    fun `deleteProduct should return false if product to delete does not exist`() {
        val isDeleted = productRepository.deleteProduct(999L)
        assertFalse(isDeleted)
    }
}
