package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.dto.request.DriverCreationRequest
import tech.jaya.ridely.controller.dto.response.AcceptResponse
import tech.jaya.ridely.controller.exception.DriverNotFoundException
import tech.jaya.ridely.controller.exception.RideNotFoundException
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo

@Service
class DriverService(
    private val driverRepo: DriverRepo,
    private val rideRepo: RideRepo
) {

    fun findById(id: Long): Driver {
        return driverRepo.findById(id).orElseThrow {
            DriverNotFoundException("Driver with id $id not found")
        }
    }

    fun getRide(id: Long): AcceptResponse {
        val ride = rideRepo.findLastRideByDriveId(id).orElseThrow {
             RideNotFoundException("You don't have any Ride")
        }
        return AcceptResponse.fromRide(ride)
    }

    fun save(driverRequest: DriverCreationRequest): Driver {
        return driverRepo.save(driverRequest.toDriver())
    }

    fun delete(id: Long) {
        driverRepo.deleteById(id)
    }
}