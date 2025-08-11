package tech.jaya.ridely.integration.googlemaps.dto.geocode

import com.fasterxml.jackson.annotation.JsonProperty

data class Location(
    @field:JsonProperty("lat") val latitude:Double,
    @field:JsonProperty("lng") val longitude:Double)
