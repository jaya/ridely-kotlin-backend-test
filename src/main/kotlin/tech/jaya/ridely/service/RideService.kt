package tech.jaya.ridely.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.dto.request.ActionRideRequest
import tech.jaya.ridely.controller.dto.request.FinishRideRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.AcceptResponse
import tech.jaya.ridely.controller.dto.response.CancelResponse
import tech.jaya.ridely.controller.dto.response.FinishResponse
import tech.jaya.ridely.controller.dto.response.RefuseResponse
import tech.jaya.ridely.controller.exception.RideNotFoundException
import tech.jaya.ridely.integration.googlemaps.GoogleMapsFeignClient
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.service.dto.RideResponseDto
import tech.jaya.ridely.util.RidelyUtil

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val driverRepo: DriverRepo,
    private val nearestDriverService: NearestDriverService,
    private val googleMapsFeignClient: GoogleMapsFeignClient
) {


    @Value("\${google.maps.api.key}")
    private val KEY: String = ""

    fun refuseRide(req: ActionRideRequest): RefuseResponse {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.refuse()
        return RefuseResponse.fromRide(rideRepo.save(ride))
    }

    fun deleteRide(req: ActionRideRequest): CancelResponse {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.cancel()
        return CancelResponse.fromRide(rideRepo.save(ride))
    }

    fun finishRide(req: FinishRideRequest): FinishResponse {
        val (id, price) = req
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.complete(price)
        return FinishResponse.fromRide(rideRepo.save(ride))
    }

    fun acceptRide(req: ActionRideRequest): AcceptResponse {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.accept()
        return AcceptResponse.fromRide(rideRepo.save(ride))
    }

    fun delete(id: Long) {
        return rideRepo.deleteById(id)
    }

    fun requestRide(payload: RidelyPayload): RideResponseDto {
        val googleMapsApiResponse =
            googleMapsFeignClient.getDirections(payload.pickUp, payload.dropOff, "driving", KEY)
        val leg = googleMapsApiResponse.routes.firstOrNull()?.legs?.firstOrNull() ?: throw RuntimeException("")
        val kmDistance = (leg.distance.value / 1000.0)
        val minutes = (leg.duration.value / 60.0)

        val originLatitude = leg.startLocation.lat
        val originLongitude = leg.startLocation.lng

        val ride = saveRideAndAssignNearestDriver(payload, originLatitude, originLongitude)

        val estimedPrice = RidelyUtil().calculaPrice(kmDistance, minutes)

        val nearbyDrivers = nearestDriverService.top3Nearest(ride.id!!)

        return RideResponseDto(
            estimedTimeMinutes = minutes.toInt(),
            kmDistance = kmDistance,
            estimedPrice = estimedPrice,
            nearbyDrivers = nearbyDrivers
        )
    }

    private fun saveRideAndAssignNearestDriver(req: RidelyPayload, originLat: Double, originLon: Double): Ride {
        val ride = Ride().apply {
            passengerName = req.passenger.name
            passengerEmail = req.passenger.email
            pickUp = req.pickUp
            dropOff = req.dropOff
            this.originLat = originLat
            this.originLon = originLon
        }
        rideRepo.save(ride)

        ride.driver =  driverRepo.findNearestByRide(ride.id!!).first()

        return rideRepo.save(ride)
    }
}