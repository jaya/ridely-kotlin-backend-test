package tech.jaya.ridely.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.service.RideService
import tech.jaya.ridely.service.dto.RideResponseDto

@RestController
@RequestMapping("/rides")
class RideController(
    private val rideService: RideService
) {

    @PostMapping("/request-driver")
    fun requestDriver(@RequestBody req: RequestDriver): RequestDriverResponse {
        return rideService.requestDriver(req)
    }

    @PostMapping("/refuse-ride")
    fun refuseRide(@RequestBody req: ActionRideRequest): RefuseResponse {
        return rideService.refuseRide(req)
    }

    @PostMapping("/cancel-ride")
    fun deleteRide(@RequestBody req: ActionRideRequest): CancelResponse {
        return rideService.deleteRide(req)
    }

    @PostMapping("/finish-ride")
    fun finishRide(@RequestBody req: FinishRideRequest): FinishResponse {
        return rideService.finishRide(req)
    }

    @PostMapping("/accept-ride")
    fun acceptRide(@RequestBody req: ActionRideRequest): AcceptResponse {
        return rideService.acceptRide(req)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        return rideService.delete(id).let {
            ResponseEntity.noContent().build()
        }
    }

    @GetMapping("/directions")
    fun requestRide(
        @RequestParam origin: String,
        @RequestParam destination: String
    ): ResponseEntity<RideResponseDto> {
        return rideService.requestRide(origin, destination).let {
            ResponseEntity.ok(it)
        }
    }
}