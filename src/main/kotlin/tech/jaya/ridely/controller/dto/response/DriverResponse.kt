package tech.jaya.ridely.controller.dto.response

import tech.jaya.ridely.controller.dto.CarDto
import tech.jaya.ridely.model.Driver

data class DriverResponse(
    val id: Long,
    val name: String,
    val available: Boolean,
    val activationDate: String,
    val car: CarDto
)

fun Driver.toResponse(): DriverResponse {
    return DriverResponse(
        id = this.id!!,
        name = this.name,
        available = this.available,
        activationDate = this.activationDate.toString(),
        car = CarDto(
            licensePlate = this.carLicensePlate,
            model = this.carModel,
            color = this.carColor
        )
    )
}
