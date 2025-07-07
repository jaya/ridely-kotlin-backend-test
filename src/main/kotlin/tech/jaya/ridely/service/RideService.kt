package tech.jaya.ridely.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import tech.jaya.ridely.dto.ActionRideRequest
import tech.jaya.ridely.exception.DriverUnavailable
import tech.jaya.ridely.dto.FinishRideRequest
import tech.jaya.ridely.dto.RequestDriver
import tech.jaya.ridely.exception.RideNotFoundException
import tech.jaya.ridely.dto.ComputeRoutesRequest
import tech.jaya.ridely.dto.ComputeRoutesResponse
import tech.jaya.ridely.dto.DestinationRequest
import tech.jaya.ridely.dto.OriginRequest
import tech.jaya.ridely.dto.RideDetailsResponse
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val driverRepo: DriverRepo
) {
    fun requestDriver(req: RequestDriver): Ride {
        val driver = driverRepo.findAvailableDriver().orElseThrow {
            throw DriverUnavailable("We do not have drivers available")
        }
        val rideDetails = rideDetails(req.pickUp, req.dropOff)
        val ride = req.toRide(driver, rideDetails)
        ride.request(driver)
        return rideRepo.save(ride)
    }

    fun refuseRide(req: ActionRideRequest): Ride {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.refuse()
        return rideRepo.save(ride)
    }

    fun deleteRide(req: ActionRideRequest): Ride {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.cancel()
        return rideRepo.save(ride)
    }

    fun finishRide(req: FinishRideRequest): Ride {
        val (id, price) = req
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.complete(price)
        return rideRepo.save(ride)
    }

    fun acceptRide(req: ActionRideRequest): Ride {
        val id = req.id
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.accept()
        return rideRepo.save(ride)
    }

    fun delete(id: Long) {
        rideRepo.deleteById(id)
    }

    fun rideDetails(origin: String, destination: String): RideDetailsResponse {
        var distance = 0.0
        var estimatedTime = 0.0
        var price = 0.0
        runBlocking {
            val response = computeRoutesApi(origin, destination)
            response?.routes?.forEach { route -> {}
                distance = route.distanceMeters.toDouble() / 1000
                val seconds = route.duration.replace("[^0-9]".toRegex(), "")
                estimatedTime = seconds.toDouble() / 60.0
                price = calculatePrice(distance, estimatedTime)
            }
        }
        return RideDetailsResponse(
            convertValues(price),
            BigDecimal(distance),
            convertValues(estimatedTime)
        )
    }

    private fun calculatePrice(distance: Double, estimatedTime: Double): Double {
        val kilometerPrice = 3.00
        val minutePrice = 2.00
        return (distance * kilometerPrice) + (estimatedTime * minutePrice);
    }

    private fun convertValues(value: Double): BigDecimal {
        return BigDecimal(value).setScale(2, RoundingMode.UP)
    }

    suspend fun computeRoutesApi(origin: String, destination: String): ComputeRoutesResponse? {
        val apiKey = "AIzaSyBByp5bEonxBomXlKjN7TQSr9fOyYIIIuw"
        val fieldMask = "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline"
        val contentType = "application/json"

        val request = ComputeRoutesRequest(
            origin = OriginRequest(origin),
            destination = DestinationRequest(destination),
            travelMode = "DRIVE"
        )

        try {
            val response = RetrofitClient.instance.computeRoutes(apiKey, fieldMask, contentType, request)

            return response

        } catch (e: Exception) {
            println("Erro na requisição: ${e.message}")
        }
        return null
    }
}