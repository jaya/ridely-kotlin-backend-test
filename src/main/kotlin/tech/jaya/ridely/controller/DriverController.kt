package tech.jaya.ridely.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.controller.dto.request.DriverCreationRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.AcceptResponse
import tech.jaya.ridely.controller.dto.response.DriverResponse
import tech.jaya.ridely.controller.dto.response.RequestDriverResponse
import tech.jaya.ridely.controller.dto.response.toResponse
import tech.jaya.ridely.service.DriverService
import tech.jaya.ridely.service.RideService

@RestController
@RequestMapping("/drivers")
class DriverController(
    private val driverService: DriverService,
    private val rideService: RideService
) {

    @PostMapping("/request-driver")
    @ResponseStatus(HttpStatus.CREATED)
    fun requestDriver(@RequestBody req: RidelyPayload): RequestDriverResponse {
        return driverService.requestDriver(req)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<DriverResponse> {
        val driver = driverService.findById(id)
        return ResponseEntity.ok(driver.toResponse())
    }

    @GetMapping("/{id}/get-rides")
    fun getRide(@PathVariable id: Long): AcceptResponse {
        return driverService.getRide(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@RequestBody driverRequest: DriverCreationRequest): ResponseEntity<DriverResponse> {
        return driverService.save(driverRequest).let {
            ResponseEntity.ok(it.toResponse())
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        driverService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
