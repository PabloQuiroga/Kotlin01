package domain.model

object AddressMock {
    fun empty() = Address(
        id = 0,
        street = "",
        suite = "",
        city = "",
        zipcode = "",
        geo = GeoMock.empty()
    )

    @SuppressWarnings("LongParameterList")
    fun create(
        id: Long = 1,
        street: String = "Kulas Light",
        suite: String = "Apt. 556",
        city: String = "Gwenborough",
        zipcode: String = "92998-3874",
        geo: Geo = GeoMock.create()
    ) = Address(
        id = id,
        street = street,
        suite = suite,
        city = city,
        zipcode = zipcode,
        geo = geo
    )
}
