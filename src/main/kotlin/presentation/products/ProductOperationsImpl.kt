package presentation.products

import domain.model.Product
import domain.usecase.ProductUseCases

class ProductOperationsImpl(private val productUseCases : ProductUseCases): ProductOperations {

    override fun getAllProducts(): List<Product> {
        return productUseCases.getAllProducts()
    }

    override fun getProductById(productId: Long): Product? {
        return productUseCases.getProductById(productId)
    }

    override fun getProductByName(productName: String): Product? {
        val products = getAllProducts()
        return products.find { it.name == productName }
    }

    override fun getProductsByCategory(categoryId: Long): List<Product> {
        val products = getAllProducts()
        return products.filter { it.category.id == categoryId }
    }

    override fun getProductsByPriceRange(minPrice: Double, maxPrice: Double): List<Product> {
        val products = getAllProducts()
        return products.filter { it.price in minPrice..maxPrice }
    }

    override fun addNewProduct(newProduct: Product): Product {
        return productUseCases.createProduct(newProduct)
    }

    override fun updateProduct(product: Product): Product? {
        return productUseCases.updateProduct(product)
    }

    override fun deleteProduct(productId: Long): Boolean {
        return productUseCases.deleteProduct(productId)
    }
}
