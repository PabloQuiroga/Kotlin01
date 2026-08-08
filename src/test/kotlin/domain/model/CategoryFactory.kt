package domain.model

object CategoryFactory {
    fun empty() = Category(
        id = 0,
        name = "",
        description = ""
    )

    fun create(
        id: Long = 1,
        name: String = "Electronics",
        description: String = "Electronic devices"
    ) = Category(
        id = id,
        name = name,
        description = description
    )

    fun createList(count: Int): List<Category> {
        return (1..count).map {
            create(
                id = it.toLong(),
                name = "Category $it",
                description = "Description for Category $it"
            )
        }
    }
}
