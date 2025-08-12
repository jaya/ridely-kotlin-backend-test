package tech.jaya.ridely.controller.dto.response

import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status

class CancelResponse  constructor(
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