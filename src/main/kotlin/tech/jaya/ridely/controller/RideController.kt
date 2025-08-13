package tech.jaya.ridely.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.ActionRideRequest
import tech.jaya.ridely.dtos.CancelResponse
import tech.jaya.ridely.dtos.FinishResponse
import tech.jaya.ridely.dtos.FinishRideRequest
import tech.jaya.ridely.dtos.RefuseResponse
import tech.jaya.ridely.dtos.RequestDriver
import tech.jaya.ridely.dtos.RequestDriverResponse
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.service.RideService
import tech.jaya.ridely.service.PassengerService

@RestController
@RequestMapping("/rides")
class RideController(
    private val rideService: RideService
    , private val passengerService: PassengerService
) {

    @PostMapping("/request-driver")
    fun requestDriver(@RequestBody req: RequestDriver): RequestDriverResponse {
        val passenger: Passenger = passengerService.getPassengerById(req.passengerId)
        return rideService.requestDriver(req, passenger)
    }

    @PostMapping("/refuse-ride")
    fun refuseRide(@RequestBody req: ActionRideRequest): RefuseResponse {
        return rideService.refuseRide(req)
    }

    @PostMapping("/cancel-ride")
    fun deleteRide(@RequestBody req: ActionRideRequest): CancelResponse {
        return rideService.cancelRide(req)
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
        rideService.delete(id)
        return ResponseEntity.noContent().build()
    }
}