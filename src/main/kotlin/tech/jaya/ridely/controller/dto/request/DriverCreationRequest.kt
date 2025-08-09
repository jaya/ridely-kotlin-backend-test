package tech.jaya.ridely.controller.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.controller.dto.CarDto
import tech.jaya.ridely.model.Driver

data class DriverCreationRequest(
    @JsonProperty(required = true)
    val name: String,
    @JsonProperty(required = true)
    val available: Boolean,
    @JsonProperty(required = true)
    val car: CarDto
) {
    fun toDriver(): Driver {
        return Driver(
            name = this.name,
            available = this.available,
            carLicensePlate = this.car.licensePlate,
            carModel = this.car.model,
            carColor = this.car.color
        )
    }
}