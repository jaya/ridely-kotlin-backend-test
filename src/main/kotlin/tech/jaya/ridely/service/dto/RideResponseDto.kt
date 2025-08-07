package tech.jaya.ridely.service.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class RideResponseDto(
    @field:JsonProperty("tempo_estimado_minutos")
    val estimedTimeMinutes: String,

    @field:JsonProperty("distancia_km")
    val kmDistance: String,

    @field:JsonProperty("preco_estimado")
    val estimedPrice: BigDecimal
)