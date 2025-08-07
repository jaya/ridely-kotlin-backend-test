package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.DriverNotFound
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.repository.DriverRepo

@Service
class DriverService(
    private val driverRepo: DriverRepo
) {

    fun findById(id: Long): Driver {
        return driverRepo.findById(id).orElseThrow {
            DriverNotFound("Drive not found $id")
        }
    }
}