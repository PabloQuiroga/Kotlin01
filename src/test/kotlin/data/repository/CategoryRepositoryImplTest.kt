package data.repository

import data.source.DatabaseManager
import domain.model.Category
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager

class CategoryRepositoryImplTest {

    private lateinit var connection: Connection
    private lateinit var categoryRepository: CategoryRepositoryImpl

    @BeforeEach
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        DatabaseManager.setTestConnection(connection)
        DatabaseManager.initDatabase() // Inicializar el esquema en la DB en memoria
        categoryRepository = CategoryRepositoryImpl()
    }

    @AfterEach
    fun tearDown() {
        connection.close()
        DatabaseManager.clearTestConnection()
    }

    @Test
    fun `addCategory should insert a category and return the category with generated ID`() {
        val newCategory = Category(name = "Electronics", description = "Devices and gadgets")
        val addedCategory = categoryRepository.addCategory(newCategory)

        assertNotNull(addedCategory.id)
        assertEquals(newCategory.name, addedCategory.name)
        assertEquals(newCategory.description, addedCategory.description)

        val retrievedCategory = categoryRepository.getCategoryById(addedCategory.id!!)
        assertEquals(addedCategory, retrievedCategory)
    }

    @Test
    fun `getAllCategories should return all categories in the database`() {
        val category1 = Category(name = "Books", description = "Literature")
        categoryRepository.addCategory(category1)
        val category2 = Category(name = "Movies", description = "Films")
        categoryRepository.addCategory(category2)

        val categories = categoryRepository.getAllCategories()
        assertEquals(2, categories.size)
        assertTrue(categories.any { it.name == "Books" })
        assertTrue(categories.any { it.name == "Movies" })
    }

    @Test
    fun `getCategoryById should return the correct category when found`() {
        val newCategory = Category(name = "Electronics", description = "Devices and gadgets")
        val addedCategory = categoryRepository.addCategory(newCategory)

        val foundCategory = categoryRepository.getCategoryById(addedCategory.id!!)
        assertEquals(addedCategory, foundCategory)
    }

    @Test
    fun `getCategoryById should return null when category not found`() {
        val foundCategory = categoryRepository.getCategoryById(999L)
        assertNull(foundCategory)
    }

    @Test
    fun `updateCategory should update an existing category and return the updated category`() {
        val newCategory = Category(name = "Electronics", description = "Devices and gadgets")
        val addedCategory = categoryRepository.addCategory(newCategory)

        val updatedCategory = addedCategory.copy(name = "Updated Electronics", description = "Updated description")
        val result = categoryRepository.updateCategory(updatedCategory)

        assertNotNull(result)
        assertEquals("Updated Electronics", result?.name)
        assertEquals("Updated description", result?.description)

        val retrievedCategory = categoryRepository.getCategoryById(addedCategory.id!!)
        assertEquals(result, retrievedCategory)
    }

    @Test
    fun `updateCategory should return null if category to update does not exist`() {
        val nonExistentCategory = Category(id = 999L, name = "Non Existent", description = "None")
        val result = categoryRepository.updateCategory(nonExistentCategory)
        assertNull(result)
    }

    @Test
    fun `deleteCategory should delete an existing category and return true`() {
        val newCategory = Category(name = "Electronics", description = "Devices and gadgets")
        val addedCategory = categoryRepository.addCategory(newCategory)

        val isDeleted = categoryRepository.deleteCategory(addedCategory.id!!)
        assertTrue(isDeleted)

        val foundCategory = categoryRepository.getCategoryById(addedCategory.id!!)
        assertNull(foundCategory)
    }

    @Test
    fun `deleteCategory should return false if category to delete does not exist`() {
        val isDeleted = categoryRepository.deleteCategory(999L)
        assertFalse(isDeleted)
    }
}