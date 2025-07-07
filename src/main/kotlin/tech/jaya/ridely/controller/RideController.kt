package tech.jaya.ridely.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.jaya.ridely.dto.AcceptResponse
import tech.jaya.ridely.dto.ActionRideRequest
import tech.jaya.ridely.dto.CancelResponse
import tech.jaya.ridely.dto.FinishResponse
import tech.jaya.ridely.dto.FinishRideRequest
import tech.jaya.ridely.dto.RefuseResponse
import tech.jaya.ridely.dto.RequestDriver
import tech.jaya.ridely.dto.RequestDriverResponse
import tech.jaya.ridely.dto.RideDetailsResponse
import tech.jaya.ridely.service.RideService

@RestController
@RequestMapping("/rides")
class RideController(
    private val rideService: RideService
) {

    @PostMapping("/request-driver")
    fun requestDriver(@RequestBody req: RequestDriver): RequestDriverResponse {
        return RequestDriverResponse.fromRide(rideService.requestDriver(req))
    }

    @PostMapping("/refuse-ride")
    fun refuseRide(@RequestBody req: ActionRideRequest): RefuseResponse {
        return RefuseResponse.fromRide(rideService.refuseRide(req))
    }

    @PostMapping("/cancel-ride")
    fun deleteRide(@RequestBody req: ActionRideRequest): CancelResponse {
        return CancelResponse.fromRide(rideService.deleteRide(req))
    }

    @PostMapping("/finish-ride")
    fun finishRide(@RequestBody req: FinishRideRequest): FinishResponse {
        return FinishResponse.fromRide(rideService.finishRide(req))
    }

    @PostMapping("/accept-ride")
    fun acceptRide(@RequestBody req: ActionRideRequest): AcceptResponse {
        return AcceptResponse.fromRide(rideService.acceptRide(req))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        return rideService.delete(id).let {
            ResponseEntity.noContent().build()
        }
    }
}