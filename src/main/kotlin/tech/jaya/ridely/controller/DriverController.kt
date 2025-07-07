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
import tech.jaya.ridely.dto.DriverCreation
import tech.jaya.ridely.dto.DriverResponse
import tech.jaya.ridely.dto.toResponse
import tech.jaya.ridely.service.DriverService

@RestController
@RequestMapping("/drivers")
class DriverController(
    private val driverService: DriverService
) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<DriverResponse> {
        return ResponseEntity.ok(driverService.findById(id).toResponse())
    }

    @GetMapping("/{id}/get-rides")
    fun getRide(@PathVariable id: Long): AcceptResponse {
        val ride = driverService.getRide(id)
        return AcceptResponse.fromRide(ride)
    }

    @PostMapping
    fun save(@RequestBody driverRequest: DriverCreation): ResponseEntity<DriverResponse> {
        return ResponseEntity.ok(driverService.save(driverRequest).toResponse())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        return driverService.delete(id).let {
            ResponseEntity.noContent().build()
        }
    }
}
