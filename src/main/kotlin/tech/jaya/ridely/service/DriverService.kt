package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.DriverCreation
import tech.jaya.ridely.controller.DriverNotFound
import tech.jaya.ridely.controller.RideNotFoundException
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo

@Service
class DriverService(
    private val driverRepo: DriverRepo,
    private val rideRepo: RideRepo
) {

    fun findById(id: Long): Driver {
        return driverRepo.findById(id).orElseThrow {
            DriverNotFound("Drive not found $id")
        }
    }

    fun getRide(id: Long): Ride {
        return rideRepo.findLastRideByDriveId(id).orElseThrow {
            throw RideNotFoundException("You don't have any Ride")
        }
    }

    fun save(driverRequest: DriverCreation): Driver {
        return driverRepo.save(driverRequest.toDriver())
    }

    fun delete(id: Long) {
        driverRepo.deleteById(id)
    }
}