package tech.jaya.ridely.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.dto.request.ActionRideRequest
import tech.jaya.ridely.controller.dto.request.FinishRideRequest
import tech.jaya.ridely.controller.dto.request.RequestDriver
import tech.jaya.ridely.controller.dto.response.*
import tech.jaya.ridely.controller.exception.DriverUnavailable
import tech.jaya.ridely.controller.exception.RideNotFoundException
import tech.jaya.ridely.integration.googlemaps.GoogleMapsFeignClient
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.service.dto.RideResponseDto
import tech.jaya.ridely.util.RideUtil

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val driverRepo: DriverRepo,
    private val googleMapsFeignClient: GoogleMapsFeignClient
) {


    @Value("\${google.maps.api.key}")
    private val KEY: String = ""

    fun requestDriver(req: RequestDriver): RequestDriverResponse {
        val driver = driverRepo.findAvailableDriver().orElseThrow {
            throw DriverUnavailable("We do not have drivers available")
        }
        val ride = req.toRide(driver)
        ride.request(driver)
        return RequestDriverResponse.fromRide(rideRepo.save(ride))
    }

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

    fun requestRide(
        origin: String, destination: String
    ): RideResponseDto {
        val googleMapsApiResponse = googleMapsFeignClient.getDirections(origin, destination, "driving", KEY)
        val leg = googleMapsApiResponse.routes.firstOrNull()?.legs?.firstOrNull() ?: throw RuntimeException("")
        val kmDistance = (leg.distance.value / 1000.0)
        val minutes = (leg.duration.value / 60.0).toInt()

        val estimedPrice = RideUtil().calculaPrice(kmDistance, minutes)

        return RideResponseDto(
            estimedTimeMinutes = String.format("%.2f", minutes),
            kmDistance = String.format("%.2f", kmDistance),
            estimedPrice
        )
    }
}