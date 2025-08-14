package tech.jaya.ridely.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tech.jaya.ridely.dtos.LocationInfo

@Component
class GoogleGeocodingService(
    @Value("\${google.maps.api.key}") private val apiKey: String
) {
    private val restClient = RestClient.builder()
        .baseUrl("https://maps.googleapis.com/maps/api")
        .defaultHeader("Accept-Language", "pt-BR")
        .build()

    fun getLocationInfo(lat: Double, lng: Double): LocationInfo? {
        val response = restClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/geocode/json")
                    .queryParam("latlng", "$lat,$lng")
                    .queryParam("key", apiKey)
                    .queryParam("region", "br")
                    .build()
            }
            .retrieve()
            .body(JsonNode::class.java)

        val results = response?.get("results") ?: return null

        var sublocality: String? = null
        var city: String? = null
        var state: String? = null
        var country: String? = null

        for (result in results) {
            val components = result.get("address_components")
            for (component in components) {
                val types = component.get("types").map { it.asText() }
                when {
                    (sublocality == null && ("sublocality" in types || "neighborhood" in types)) ->
                        sublocality = component.get("long_name").asText()
                    (city == null && "locality" in types) ->
                        city = component.get("long_name").asText()
                    (state == null && "administrative_area_level_1" in types) ->
                        state = component.get("short_name").asText()
                    (country == null && "country" in types) ->
                        country = component.get("long_name").asText()
                }
            }

            if (sublocality != null && city != null && state != null && country != null) break
        }

        return LocationInfo(
            sublocality = sublocality,
            city = city,
            state = state,
            country = country
        )
    }
}


