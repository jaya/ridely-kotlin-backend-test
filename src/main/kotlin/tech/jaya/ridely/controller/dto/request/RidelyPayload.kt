package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Ride

class RidelyPayload(
    @JsonProperty(required = true)
    val passenger: PassengerRequest,
    @JsonProperty(required = true)
    val pickUp: String,
    @JsonProperty(required = true)
    val dropOff: String
) {
    fun toRide(driver: Driver) = Ride(
        pickUp = this.pickUp,
        dropOff = this.dropOff,
        passengerName = passenger.name,
        passengerEmail = passenger.email,
        driver = driver,
        originLat = 0.0,
        originLon = 0.0
    )
}

