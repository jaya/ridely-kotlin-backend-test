package tech.jaya.ridely.service.dto

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.controller.dto.NearbyDriverDto
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Ride
import java.math.BigDecimal

data class RideResponseDto(
    @field:JsonProperty("tempo_estimado_minutos")
    val estimedTimeMinutes: Int,

    @field:JsonProperty("distancia_km")
    val kmDistance: Double,

    @field:JsonProperty("preco_estimado")
    val estimedPrice: BigDecimal,

    val nearbyDrivers:List<NearbyDriverDto>
)