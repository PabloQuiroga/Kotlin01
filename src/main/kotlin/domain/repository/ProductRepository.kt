package domain.repository

import domain.model.Product

interface ProductRepository {
    fun getAllProducts(): List<Product>
    fun getProductById(id: Long): Product?

    fun addProduct(product: Product): Product
    fun updateProduct(product: Product): Product?
    fun deleteProduct(id: Long): Boolean
}
