package presentation.products

import domain.model.Product

interface ProductOperations {
    fun getAllProducts(): List<Product>
    fun getProductById(productId: Long): Product?
    fun getProductByName(productName: String): Product?
    fun getProductsByCategory(categoryId: Long): List<Product>
    fun getProductsByPriceRange(minPrice: Double, maxPrice: Double): List<Product>

    fun addNewProduct(newProduct: Product): Product
    fun updateProduct(product: Product): Product?
    fun deleteProduct(productId: Long): Boolean
}

