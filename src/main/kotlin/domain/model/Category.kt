package domain.model

data class Category(
    val id: Long? = null, // Cambiado a Long?
    val name: String,
    val description: String
)
