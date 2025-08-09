package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

class PassengerRequest(
    @JsonProperty(required = true)
    val name: String,
    @JsonProperty(required = true)
    val email: String
)
