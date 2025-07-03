package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.ActionRideRequest
import tech.jaya.ridely.controller.DriverUnavailable
import tech.jaya.ridely.controller.FinishRideRequest
import tech.jaya.ridely.controller.RequestDriver
import tech.jaya.ridely.controller.RideNotFoundException
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val driverRepo: DriverRepo
) {
    fun requestDriver(req: RequestDriver): Ride {
        val driver = driverRepo.findAvailableDriver().orElseThrow {
            throw DriverUnavailable("We do not have drivers available")
        }
        val ride = req.toRide(driver)
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
}