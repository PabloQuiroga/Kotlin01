package domain.model

data class Product(
    val id: Long? = null, // Añadimos ID nullable
    val name: String,
    val price: Double,
    val category: Category
)