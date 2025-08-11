package tech.jaya.ridely.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.controller.dto.request.ActionRideRequest
import tech.jaya.ridely.controller.dto.request.FinishRideRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.*
import tech.jaya.ridely.service.RideService
import tech.jaya.ridely.service.dto.RideResponseDto

@RestController
@RequestMapping("/rides")
class RideController(
    private val rideService: RideService
) {


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

    @PostMapping("/request-ride")
    fun requestRide(
        @Valid @RequestBody requestRide: RidelyPayload
    ): ResponseEntity<RideResponseDto> {
        return rideService.requestRide(requestRide).let {
            ResponseEntity.ok(it)
        }
    }
}