package domain.usecase

import domain.model.Category
import domain.repository.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CategoryUseCasesTest {

    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryUseCases: CategoryUseCases

    @BeforeEach
    fun setUp() {
        categoryRepository = mockk()
        categoryUseCases = CategoryUseCases(categoryRepository)
    }

    @Test
    fun `getAllCategories should return a list of categories from the repository`() {
        // Given
        val expectedCategories = listOf(
            Category(id = 1, name = "Electronics", description = "Devices"),
            Category(id = 2, name = "Books", description = "Literature")
        )
        every { categoryRepository.getAllCategories() } returns expectedCategories

        // When
        val actualCategories = categoryUseCases.getAllCategories()

        // Then
        assertEquals(expectedCategories, actualCategories)
        verify(exactly = 1) { categoryRepository.getAllCategories() }
    }

    @Test
    fun `getCategoryById should return a category from the repository when found`() {
        // Given
        val categoryId = 1L
        val expectedCategory = Category(id = categoryId, name = "Electronics", description = "Devices")
        every { categoryRepository.getCategoryById(categoryId) } returns expectedCategory

        // When
        val actualCategory = categoryUseCases.getCategoryById(categoryId)

        // Then
        assertEquals(expectedCategory, actualCategory)
        verify(exactly = 1) { categoryRepository.getCategoryById(categoryId) }
    }

    @Test
    fun `getCategoryById should return null from the repository when not found`() {
        // Given
        val categoryId = 99L
        every { categoryRepository.getCategoryById(categoryId) } returns null

        // When
        val actualCategory = categoryUseCases.getCategoryById(categoryId)

        // Then
        assertEquals(null, actualCategory)
        verify(exactly = 1) { categoryRepository.getCategoryById(categoryId) }
    }

    @Test
    fun `createCategory should add a category to the repository and return the added category`() {
        // Given
        val categoryToCreate = Category(name = "New Category", description = "Description for new category")
        val expectedCategory = categoryToCreate.copy(id = 3) // Simulate ID being assigned by repository
        every { categoryRepository.addCategory(categoryToCreate) } returns expectedCategory

        // When
        val actualCategory = categoryUseCases.createCategory(categoryToCreate)

        // Then
        assertEquals(expectedCategory, actualCategory)
        verify(exactly = 1) { categoryRepository.addCategory(categoryToCreate) }
    }

    @Test
    fun `updateCategory should update a category in the repository and return the updated category`() {
        // Given
        val categoryToUpdate = Category(id = 1, name = "Updated Electronics", description = "Updated Devices")
        every { categoryRepository.updateCategory(categoryToUpdate) } returns categoryToUpdate

        // When
        val actualCategory = categoryUseCases.updateCategory(categoryToUpdate)

        // Then
        assertEquals(categoryToUpdate, actualCategory)
        verify(exactly = 1) { categoryRepository.updateCategory(categoryToUpdate) }
    }

    @Test
    fun `updateCategory should return null if the category does not exist in the repository`() {
        // Given
        val categoryToUpdate = Category(id = 99, name = "NonExistent", description = "None")
        every { categoryRepository.updateCategory(categoryToUpdate) } returns null

        // When
        val actualCategory = categoryUseCases.updateCategory(categoryToUpdate)

        // Then
        assertEquals(null, actualCategory)
        verify(exactly = 1) { categoryRepository.updateCategory(categoryToUpdate) }
    }

    @Test
    fun `deleteCategory should delete a category from the repository and return true`() {
        // Given
        val categoryId = 1L
        every { categoryRepository.deleteCategory(categoryId) } returns true

        // When
        val result = categoryUseCases.deleteCategory(categoryId)

        // Then
        assertEquals(true, result)
        verify(exactly = 1) { categoryRepository.deleteCategory(categoryId) }
    }

    @Test
    fun `deleteCategory should return false if the category does not exist in the repository`() {
        // Given
        val categoryId = 99L
        every { categoryRepository.deleteCategory(categoryId) } returns false

        // When
        val result = categoryUseCases.deleteCategory(categoryId)

        // Then
        assertEquals(false, result)
        verify(exactly = 1) { categoryRepository.deleteCategory(categoryId) }
    }
}
