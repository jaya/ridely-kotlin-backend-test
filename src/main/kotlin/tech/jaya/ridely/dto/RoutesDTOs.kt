package tech.jaya.ridely.dto

import com.google.gson.annotations.SerializedName

class ComputeRoutesRequest(
    @SerializedName("origin") val origin: OriginRequest,
    @SerializedName("destination") val destination: DestinationRequest,
    @SerializedName("travelMode") val travelMode: String = "DRIVE",
    @SerializedName("routingPreference") val routingPreference: String = "TRAFFIC_AWARE"
)

data class OriginRequest(
    @SerializedName("address") val address: String
)

data class DestinationRequest(
    @SerializedName("address") val address: String
)

data class ComputeRoutesResponse(
    @SerializedName("routes") val routes: List<RouteResponse>
)

data class RouteResponse(
    @SerializedName("distanceMeters") val distanceMeters: Int,
    @SerializedName("duration") val duration: String,
    @SerializedName("polyline") val polyline: PolylineResponse
)

data class PolylineResponse(
    @SerializedName("encodedPolyline") val encodedPolyline: String
)