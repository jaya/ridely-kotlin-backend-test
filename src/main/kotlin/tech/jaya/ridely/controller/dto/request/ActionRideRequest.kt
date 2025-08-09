package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class ActionRideRequest(
    @JsonProperty(required = true)
    val id: Long
)

