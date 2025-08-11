package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.dto.NearbyDriverDto
import tech.jaya.ridely.repository.DriverRepo

@Service
class NearestDriverService(
   private val driverRepo: DriverRepo
) {

    fun top3Nearest(rideId: Long): List<NearbyDriverDto> {
        val rows = driverRepo.findNearestByRide(rideId)
        return rows.map {
           NearbyDriverDto(
                driverId = it.id!!,
                name = it.name
            )
        }
    }
}
