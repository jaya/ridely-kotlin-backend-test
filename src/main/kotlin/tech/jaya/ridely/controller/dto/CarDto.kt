package tech.jaya.ridely.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CarDto(
    @JsonProperty(required = true)
    val licensePlate: String,
    @JsonProperty(required = true)
    val model: String,
    @JsonProperty(required = true)
    val color: String
)

