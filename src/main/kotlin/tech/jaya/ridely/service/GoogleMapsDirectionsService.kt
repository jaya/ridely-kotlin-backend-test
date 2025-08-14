package tech.jaya.ridely.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class GoogleMapsService(
    @Value("\${google.maps.api.key}") private val apiKey: String
) {
    private val restClient = RestClient.builder()
        .baseUrl("https://maps.googleapis.com/maps/api")
        .defaultHeader("Accept-Language", "pt-BR")
        .build()

    fun getRouteInfo(origin: String, destination: String): Pair<Int, Int> {
        val response = restClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/directions/json")
                    .queryParam("origin", origin)
                    .queryParam("destination", destination)
                    .queryParam("region", "br")
                    .queryParam("key", apiKey)
                    .build()
            }
            .retrieve()
            .body(JsonNode::class.java)

        val route = response?.get("routes")?.get(0)
        val leg = route?.get("legs")?.get(0)
        val distance = leg?.get("distance")?.get("value")?.asInt() ?: 0
        val duration = leg?.get("duration")?.get("value")?.asInt() ?: 0
        return Pair(distance, duration)
    }
}

