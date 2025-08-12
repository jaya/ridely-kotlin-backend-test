package tech.jaya.ridely.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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


    @PatchMapping("/{id}/refuse-ride")
    fun refuseRide(@PathVariable id: Long): RefuseResponse {
        return rideService.refuseRide(id)
    }

    @PatchMapping("/{id}/cancel-ride")
    fun deleteRide(@PathVariable id: Long): CancelResponse {
        return rideService.deleteRide(id)
    }

    @PatchMapping("/{id}/finish-ride")
    fun finishRide(@PathVariable id: Long, @RequestBody req: FinishRideRequest): FinishResponse {
        return rideService.finishRide(id, req)
    }

    @PatchMapping("/{id}/accept-ride")
    fun acceptRide(@PathVariable id: Long): AcceptResponse {
        return rideService.acceptRide(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        }
    }
}