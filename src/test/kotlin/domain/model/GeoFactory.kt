package domain.model

object GeoFactory {
    fun empty() = Geo(
        id = 0,
        lat = "",
        lng = ""
    )

    fun create(
        id: Long = 1,
        lat: String = "-37.3159",
        lng: String = "81.1496"
    ) = Geo(
        id = id,
        lat = lat,
        lng = lng
    )
}
