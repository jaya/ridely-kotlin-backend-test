package tech.jaya.ridely.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.DriverCreation
import tech.jaya.ridely.dtos.DriverResponse
import tech.jaya.ridely.service.DriverService

@RestController
@RequestMapping("/drivers")
class DriverController(
    private val driverService: DriverService
) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<DriverResponse> {
        val response = driverService.findById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/get-rides")
    fun getRide(@PathVariable id: Long): AcceptResponse {
        return driverService.getLastRide(id)
    }

    @PostMapping
    fun save(@RequestBody driverRequest: DriverCreation): ResponseEntity<DriverResponse> {
        val response = driverService.save(driverRequest)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        driverService.delete(id)
        return ResponseEntity.noContent().build()
    }
}