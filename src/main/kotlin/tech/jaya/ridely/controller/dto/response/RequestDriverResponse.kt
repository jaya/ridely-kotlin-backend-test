package tech.jaya.ridely.controller.dto.response

import tech.jaya.ridely.controller.dto.CarDto
import tech.jaya.ridely.controller.dto.DriverDto
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status

class RequestDriverResponse (
    val id: Long,
    val driver: DriverDto,
    val status: Status,
    val dropOff: String,
    val pickUp: String,
) {
    companion object {
        fun fromRide(ride: Ride) = RequestDriverResponse(
            id = ride.id!!,
            dropOff = ride.dropOff!!,
            pickUp = ride.pickUp!!,
            status = ride.status!!,
            driver = DriverDto(
                name = ride.driver!!.name,
                car = CarDto(
                    licensePlate = ride.driver!!.carLicensePlate,
                    model = ride.driver!!.carModel,
                    color = ride.driver!!.carColor
                )
            ),
        )
    }
}
