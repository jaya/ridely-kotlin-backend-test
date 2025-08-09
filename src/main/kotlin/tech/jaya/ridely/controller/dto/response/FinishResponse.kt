package tech.jaya.ridely.controller.dto.response

import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status
import java.math.BigDecimal

class FinishResponse private constructor(
    val id: Long,
    val passenger: PassengerResponse,
    val dropOff: String,
    val status: Status,
    val price: BigDecimal
) {

    companion object {
        fun fromRide(ride: Ride): FinishResponse {
            return FinishResponse(
                id = ride.id!!,
                passenger = PassengerResponse(ride.passengerName!!, ride.passengerEmail!!),
                dropOff = ride.dropOff!!,
                status = ride.status!!,
                price = ride.price!!
            )
        }
    }
}