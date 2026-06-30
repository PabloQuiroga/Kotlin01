package presentation.products.dtos

import domain.model.Product
import presentation.categories.dtos.CategoryDto

data class ProductDto(
    val name: String,
    val price: Double,
    val category: CategoryDto
)

/**
 * Mappers
 */
fun ProductDto.toDomain(): Product {
    // TODO implementar
    return TODO("Provide the return value")
}

fun Product.toDto(): ProductDto {
    // TODO implementar
    return TODO("Provide the return value")
}

