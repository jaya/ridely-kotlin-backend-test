package tech.jaya.ridely.controller.dto.response

import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status

class RefuseResponse private constructor(
    val id: Long,
    val passenger: PassengerResponse,
    val pickUp: String,
    val dropOff: String,
    val status: Status
) {
    companion object {
        fun fromRide(ride: Ride) = RefuseResponse(
            id = ride.id!!,
            passenger = PassengerResponse(ride.passengerName!!, ride.passengerEmail!!),
            pickUp = ride.pickUp!!,
            dropOff = ride.dropOff!!,
            status = ride.status!!
        )
    }
}