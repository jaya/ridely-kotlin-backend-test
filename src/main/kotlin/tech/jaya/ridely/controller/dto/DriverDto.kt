package tech.jaya.ridely.controller.dto

data class DriverDto(
    val name: String,
    val car: CarDto
) {
    data class CarDto(
        val licensePlate: String,
        val model: String,
        val color: String
    )
}