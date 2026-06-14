package domain.model

data class Address(
    val id: Long? = null, // Añadimos ID nullable
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo // Incluimos Geo aquí
)
