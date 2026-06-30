package presentation.users.dtos

import domain.model.User

data class UserDto(
    val id: String?,
    val name: String,
    val email: String,
    val phone: String? = null,
    val website: String? = null,
    val address: AddressDto? = null
)

/**
 * Mappers
 */
fun UserDto.toDomain(): User {
    // TODO implementar
    return TODO("Provide the return value")
}

fun User.toDto(): UserDto {
    // TODO implementar
    return TODO("Provide the return value")
}
