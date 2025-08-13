package tech.jaya.ridely.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.ridely.model.Passenger

data class PassengerCreation(
    @JsonProperty(required = true)
    val name: String,
    @JsonProperty(required = true)
    val inTraveling: Boolean,
) {
    fun toPassenger(): Passenger {
        return Passenger(
            name = this.name,
            inTraveling = this.inTraveling,
        )
    }
}

data class PassengerResponse(
    val id: Long,
    val name: String,
    val email: String,
    val inTraveling: Boolean,
    val activationDate: String,
)

fun Passenger.toResponse(): PassengerResponse {
    return PassengerResponse(
        id = this.id!!,
        name = this.name,
        email = this.email,
        inTraveling = this.inTraveling,
        activationDate = this.activationDate.toString(),
    )
}