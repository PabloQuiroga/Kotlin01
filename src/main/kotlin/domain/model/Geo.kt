package domain.model

data class Geo(
    val id: Long? = null, // Añadimos ID nullable
    val lat: String,
    val lng: String
)
