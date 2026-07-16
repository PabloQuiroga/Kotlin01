package presentation.users.dtos

import domain.model.Geo

data class GeoDto(
    val lat: String,
    val lng: String
)

/**
 * Mappers
 */
fun Geo.toDto(): GeoDto {
    return GeoDto(
        lat = this.lat,
        lng = this.lng
    )
}

fun GeoDto.toDomain(): Geo {
    return Geo(
        lat = this.lat,
        lng = this.lng
    )
}
