package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.controller.dto.CarDto
import tech.jaya.ridely.model.Driver
import java.time.LocalDateTime

data class DriverCreationRequest(
    @JsonProperty(required = true)
    val name: String,
    @JsonProperty(required = true)
    val available: Boolean,
    @JsonProperty(required = true)
    val address: String,
    val car: CarDto
) {
    fun toDriver(lat:Double, lon:Double): Driver {
        return Driver(
            name = this.name,
            available = this.available,
            activationDate = LocalDateTime.now(),
            carLicensePlate = this.car.licensePlate,
            carModel = this.car.model,
            carColor = this.car.color,
            locationUpdatedAt = LocalDateTime.now(),
            latitude = lat,
            longitude = lon
        )
    }
}