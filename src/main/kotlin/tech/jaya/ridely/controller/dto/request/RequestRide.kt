package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RequestRide(
    @field:NotBlank
    @field:JsonProperty("pick_up", required = true)
    val pickUp: String,

    @field:NotBlank
    @field:JsonProperty("drop_off", required = true)
    val dropOff: String,

    @field:NotBlank
    @JsonProperty("passenger_name", required = true)
    val passengerName: String,

    @field:NotBlank
    @JsonProperty("passenger_email", required = true)
    val passengerEmail: String
)