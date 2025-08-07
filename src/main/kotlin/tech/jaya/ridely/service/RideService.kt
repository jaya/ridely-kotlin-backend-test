package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.integration.googlemaps.GoogleMapsFeignClient
import tech.jaya.ridely.service.dto.RideResponseDto
import tech.jaya.ridely.util.RideUtil

@Service
class RideService(
    private val googleMapsFeignClient: GoogleMapsFeignClient
) {

    fun requestRide(
        origin: String, destination: String, key: String
    ): RideResponseDto {
        val googleMapsApiResponse = googleMapsFeignClient.getDirections(origin, destination, key);
        val leg = googleMapsApiResponse.routeDtos.firstOrNull()?.legDtos?.firstOrNull() ?: throw RuntimeException("")
        val kmDistance = (leg.distanceDto.value / 1000.0)
        val estimedTimeMinutes = leg.durationDto.value / 60.0

        val estimedPrice = RideUtil().calculaPrice(kmDistance, estimedTimeMinutes)

        return RideResponseDto(
            estimedTimeMinutes = String.format("%.2f", estimedTimeMinutes),
            kmDistance = String.format("%.2f", kmDistance),
            estimedPrice
        )
    }
}