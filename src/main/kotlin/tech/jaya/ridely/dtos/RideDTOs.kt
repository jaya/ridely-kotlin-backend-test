package tech.jaya.ridely.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status
import java.math.BigDecimal

data class RequestDriver(
    @JsonProperty("passengerId", required = true)
    val passengerId: Long,
    @JsonProperty("pickUp", required = true)
    val pickUp: String,
    @JsonProperty("dropOff", required = true)
    val dropOff: String
) {
    fun toRide(driver: Driver, passenger: Passenger) = Ride(
        pickUp = this.pickUp,
        dropOff = this.dropOff,
        passenger = passenger,
        driver = driver
    )
}

data class FinishRideRequest(
    @JsonProperty(required = true)
    val id: Long,
    @JsonProperty(required = true)
    val price: BigDecimal
)

data class ActionRideRequest(
    @JsonProperty(required = true)
    val id: Long
)

class RequestPassengerResponse(
    val name: String,
    val email: String
) {
    companion object {
        fun fromPassenger(passenger: Passenger) = RequestPassengerResponse(
            name = passenger.name,
            email = passenger.email
        )
    }
}

class RequestDriverResponse private constructor(
    val id: Long,
    val driver: DriverDto,
    val status: Status,
    val pickUp: String,
    val dropOff: String,
    val distance: Int,
    val duration: Int

) {
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

    companion object {
        fun fromRide(ride: Ride) = RequestDriverResponse(
            id = ride.id!!,
            dropOff = ride.dropOff!!,
            pickUp = ride.pickUp!!,
            status = ride.status!!,
            distance = ride.distance ?: 0,
            duration = ride.duration ?: 0,
            driver = DriverDto(
                name = ride.driver!!.name,
                car = DriverDto.CarDto(
                    licensePlate = ride.driver!!.carLicensePlate,
                    model = ride.driver!!.carModel,
                    color = ride.driver!!.carColor
                )
            ),
        )
    }
}


class FinishResponse private constructor(
    val id: Long,
    val passenger: RequestPassengerResponse,
    val dropOff: String,
    val status: Status,
    val price: BigDecimal
) {

    companion object {
        fun fromRide(ride: Ride): FinishResponse {
            return FinishResponse(
                id = ride.id!!,
                passenger = RequestPassengerResponse.fromPassenger(ride.passenger!!),
                dropOff = ride.dropOff!!,
                status = ride.status!!,
                price = ride.price!!
            )
        }
    }
}

class RefuseResponse private constructor(
    val id: Long,
    val passenger: RequestPassengerResponse,
    val pickUp: String,
    val dropOff: String,
    val status: Status
) {
    companion object {
        fun fromRide(ride: Ride) = RefuseResponse(
            id = ride.id!!,
            passenger = RequestPassengerResponse.fromPassenger(ride.passenger!!),
            pickUp = ride.pickUp!!,
            dropOff = ride.dropOff!!,
            status = ride.status!!
        )
    }
}

class CancelResponse private constructor(
    val id: Long,
    val pickUp: String,
    val dropOff: String,
    val status: Status
) {
    companion object {
        fun fromRide(ride: Ride) = CancelResponse(
            id = ride.id!!,
            pickUp = ride.pickUp!!,
            dropOff = ride.dropOff!!,
            status = ride.status!!
        )
    }
}

class AcceptResponse private constructor(
    val id: Long,
    val passenger: RequestPassengerResponse,
    val pickUp: String,
    val dropOff: String,
    val status: Status,
) {
    companion object {
        fun fromRide(ride: Ride) = AcceptResponse(
            id = ride.id!!,
            passenger = RequestPassengerResponse.fromPassenger(ride.passenger!!),
            pickUp = ride.pickUp!!,
            dropOff = ride.dropOff!!,
            status = ride.status!!
        )
    }
}