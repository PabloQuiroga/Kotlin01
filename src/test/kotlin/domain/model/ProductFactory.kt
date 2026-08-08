package domain.model

object ProductFactory {
    fun empty() = Product(
        id = 0,
        name = "",
        price = 0.0,
        category = CategoryFactory.empty()
    )

    fun create(
        id: Long = 1,
        name: String = "Product 1",
        price: Double = 10.0,
        category: Category = CategoryFactory.create()
    ) = Product(
        id = id,
        name = name,
        price = price,
        category = category
    )

    fun createList(count: Int): List<Product> {
        return (1..count).map {
            create(
                id = it.toLong(),
                name = "Product $it",
                price = it * 10.0,
                category = CategoryFactory.create()
            )
        }
    }
}
