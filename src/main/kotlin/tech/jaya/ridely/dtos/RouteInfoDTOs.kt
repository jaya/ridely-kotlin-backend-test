package tech.jaya.ridely.dtos

data class RouteInfo(
    val distance: Int, // metros
    val duration: Int, // segundos
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double
)

data class LocationInfo(
    val sublocality: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null
)