package tech.jaya.ridely.integration.googlemaps.dto.directions

import com.fasterxml.jackson.annotation.JsonProperty

data class Leg(
    val distance: Distance,
    val duration: Duration,
    @field:JsonProperty("start_location") val startLocation: StartLocation,
    @field:JsonProperty("end_location") val endLocation: EndLocation
)
