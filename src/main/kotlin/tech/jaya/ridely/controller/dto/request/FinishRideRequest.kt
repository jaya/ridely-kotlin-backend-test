package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class FinishRideRequest(
    @JsonProperty(required = true)
    val id: Long,
    @JsonProperty(required = true)
    val price: BigDecimal
)

