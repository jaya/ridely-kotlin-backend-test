package tech.jaya.ridely.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jaya.ridely.controller.dto.request.DriverCreationRequest
import tech.jaya.ridely.controller.dto.response.AcceptResponse
import tech.jaya.ridely.controller.dto.response.DriverResponse
import tech.jaya.ridely.controller.dto.response.toResponse
import tech.jaya.ridely.service.DriverService

@RestController
@RequestMapping("/drivers")
class DriverController(
    private val driverService: DriverService
) {

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
    fun save(@RequestBody driverRequest: DriverCreationRequest): ResponseEntity<DriverResponse> {
        return driverService.save(driverRequest).let {
            ResponseEntity.ok(it.toResponse())
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        driverService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
