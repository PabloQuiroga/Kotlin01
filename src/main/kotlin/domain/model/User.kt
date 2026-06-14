package domain.model

data class User(
    val id: Long? = null, // Ahora es nullable
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val address: Address // Geo se manejará dentro de Address
)