package presentation.categories.dtos

import domain.model.Category

data class CategoryDto(
    val name: String,
    val description: String
)

/**
 * Mappers
 */
fun CategoryDto.toDomain(): Category {
    // TODO implementar
    return TODO("Provide the return value")
}

fun Category.toDto(): CategoryDto {
    // TODO implementar
    return TODO("Provide the return value")
}
