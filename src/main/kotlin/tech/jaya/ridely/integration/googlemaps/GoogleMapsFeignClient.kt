package tech.jaya.ridely.integration.googlemaps

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import tech.jaya.ridely.integration.googlemaps.dto.GoogleMapsApiResponse

@FeignClient(name = "googleMaps", url = "https://maps.googleapis.com")
interface GoogleMapsFeignClient {


    @GetMapping("/maps/api/directions/json")
    fun getDirections(
        @RequestParam origin: String,
        @RequestParam destination: String,
        @RequestParam mode: String,
        @RequestParam key: String
    ): GoogleMapsApiResponse
}