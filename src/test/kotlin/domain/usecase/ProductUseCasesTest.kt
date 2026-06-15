package domain.usecase

import domain.model.Category
import domain.model.Product
import domain.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductUseCasesTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productUseCases: ProductUseCases

    // Sample Category for products
    private val sampleCategory = Category(id = 1, name = "Electronics", description = "Electronic devices")

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        productUseCases = ProductUseCases(productRepository)
    }

    @Test
    fun `getAllProducts should return a list of products from the repository`() {
        // Given
        val expectedProducts = listOf(
            Product(id = 1, name = "Laptop", price = 1200.0, category = sampleCategory),
            Product(id = 2, name = "Mouse", price = 25.0, category = sampleCategory)
        )
        every { productRepository.getAllProducts() } returns expectedProducts

        // When
        val actualProducts = productUseCases.getAllProducts()

        // Then
        assertEquals(expectedProducts, actualProducts)
        verify(exactly = 1) { productRepository.getAllProducts() }
    }

    @Test
    fun `getProductById should return a product from the repository when found`() {
        // Given
        val productId = 1L
        val expectedProduct = Product(id = productId, name = "Laptop", price = 1200.0, category = sampleCategory)
        every { productRepository.getProductById(productId) } returns expectedProduct

        // When
        val actualProduct = productUseCases.getProductById(productId)

        // Then
        assertEquals(expectedProduct, actualProduct)
        verify(exactly = 1) { productRepository.getProductById(productId) }
    }

    @Test
    fun `getProductById should return null from the repository when not found`() {
        // Given
        val productId = 99L
        every { productRepository.getProductById(productId) } returns null

        // When
        val actualProduct = productUseCases.getProductById(productId)

        // Then
        assertEquals(null, actualProduct)
        verify(exactly = 1) { productRepository.getProductById(productId) }
    }

    @Test
    fun `createProduct should add a product to the repository and return the added product`() {
        // Given
        val productToCreate = Product(name = "Keyboard", price = 75.0, category = sampleCategory)
        val expectedProduct = productToCreate.copy(id = 3) // Simulate ID being assigned by repository
        every { productRepository.addProduct(productToCreate) } returns expectedProduct

        // When
        val actualProduct = productUseCases.createProduct(productToCreate)

        // Then
        assertEquals(expectedProduct, actualProduct)
        verify(exactly = 1) { productRepository.addProduct(productToCreate) }
    }

    @Test
    fun `updateProduct should update a product in the repository and return the updated product`() {
        // Given
        val productToUpdate = Product(id = 1, name = "Updated Laptop", price = 1250.0, category = sampleCategory)
        every { productRepository.updateProduct(productToUpdate) } returns productToUpdate

        // When
        val actualProduct = productUseCases.updateProduct(productToUpdate)

        // Then
        assertEquals(productToUpdate, actualProduct)
        verify(exactly = 1) { productRepository.updateProduct(productToUpdate) }
    }

    @Test
    fun `updateProduct should return null if the product does not exist in the repository`() {
        // Given
        val productToUpdate = Product(id = 99, name = "NonExistent", price = 0.0, category = sampleCategory)
        every { productRepository.updateProduct(productToUpdate) } returns null

        // When
        val actualProduct = productUseCases.updateProduct(productToUpdate)

        // Then
        assertEquals(null, actualProduct)
        verify(exactly = 1) { productRepository.updateProduct(productToUpdate) }
    }

    @Test
    fun `deleteProduct should delete a product from the repository and return true`() {
        // Given
        val productId = 1L
        every { productRepository.deleteProduct(productId) } returns true

        // When
        val result = productUseCases.deleteProduct(productId)

        // Then
        assertEquals(true, result)
        verify(exactly = 1) { productRepository.deleteProduct(productId) }
    }

    @Test
    fun `deleteProduct should return false if the product does not exist in the repository`() {
        // Given
        val productId = 99L
        every { productRepository.deleteProduct(productId) } returns false

        // When
        val result = productUseCases.deleteProduct(productId)

        // Then
        assertEquals(false, result)
        verify(exactly = 1) { productRepository.deleteProduct(productId) }
    }
}
