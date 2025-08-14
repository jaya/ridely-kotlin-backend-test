package tech.jaya.ridely.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.service.PassengerService

@RestController
@RequestMapping("/api/passengers")
class PassengerController(
    private val passengerService: PassengerService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPassenger(@RequestBody passenger: Passenger): Passenger {
        return passengerService.save(passenger)
    }

    @GetMapping("/{id}")
    fun getPassenger(@PathVariable id: Long): Passenger {
        return passengerService.getPassengerById(id)
    }

    @PutMapping("/{id}")
    fun updatePassenger(@PathVariable id: Long, @RequestBody updated: Passenger): Passenger {
        return passengerService.updatePassenger(id, updated)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePassenger(@PathVariable id: Long) {
        passengerService.delete(id)
    }
}