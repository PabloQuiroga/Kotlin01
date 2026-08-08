package domain.model

object UserFactory {
    fun empty() = User(
        id = 0,
        name = "",
        username = "",
        email = "",
        phone = "",
        website = "",
        address = AddressFactory.empty()
    )

    @SuppressWarnings("LongParameterList")
    fun create(
        id: Long = 1,
        name: String = "Leanne Graham",
        username: String = "Bret",
        email: String = "Sincere@april.biz",
        phone: String = "1-770-736-8031 x56442",
        website: String = "hildegard.org",
        address: Address = AddressFactory.create()
    ) = User(
        id = id,
        name = name,
        username = username,
        email = email,
        phone = phone,
        website = website,
        address = address
    )

    fun createList(count: Int): List<User> {
        return (1..count).map {
            create(
                id = it.toLong(),
                name = "User $it",
                username = "user$it",
                email = "user$it@example.com"
            )
        }
    }
}
