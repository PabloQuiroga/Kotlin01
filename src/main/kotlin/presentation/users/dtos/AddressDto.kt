package presentation.users.dtos

import domain.model.Address

data class AddressDto(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: GeoDto
)

/**
 * Mappers
 */
fun Address.toDto(): AddressDto {
    return AddressDto(
        street = this.street,
        suite = this.suite,
        city = this.city,
        zipcode = this.zipcode,
        geo = this.geo.toDto()
    )
}

fun AddressDto.toDomain(): Address {
    return Address(
        street = this.street,
        suite = this.suite,
        city = this.city,
        zipcode = this.zipcode,
        geo = this.geo.toDomain()
    )
}
