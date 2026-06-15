package domain.usecase

import domain.model.Product
import domain.repository.ProductRepository

class ProductUseCases(private val productRepository: ProductRepository) {

    fun getAllProducts(): List<Product> {
        return productRepository.getAllProducts()
    }

    fun getProductById(id: Long): Product? {
        return productRepository.getProductById(id)
    }

    fun createProduct(product: Product): Product {
        // Aquí podrías añadir lógica de negocio adicional antes de guardar
        return productRepository.addProduct(product)
    }

    fun updateProduct(product: Product): Product? {
        // Aquí podrías añadir lógica de negocio adicional antes de actualizar
        return productRepository.updateProduct(product)
    }

    fun deleteProduct(id: Long): Boolean {
        // Aquí podrías añadir lógica de negocio adicional antes de eliminar
        return productRepository.deleteProduct(id)
    }
}
