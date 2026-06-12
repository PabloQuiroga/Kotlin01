package domain.model

data class User(
    val id: Long = 0, // Usamos Long para IDs de base de datos
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val address: Address,
    val geo: Geo
)
