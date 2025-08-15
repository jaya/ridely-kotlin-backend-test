package tech.jaya.ridely.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tech.jaya.ridely.dtos.*
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.service.DriverService
import java.time.LocalDateTime

class DriverControllerTest {

    private val driverService = mockk<DriverService>()
    private val controller = DriverController(driverService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    @Test
    fun `deve buscar motorista por id e retornar 200`() {
        val driver = Driver(1L, "João", true, LocalDateTime.now(), "ABC1234", "Gol", "Preto")
        every { driverService.findById(1L) } returns driver.toResponse()

        mockMvc.get("/drivers/1")
            .andExpect {
                status { isOk() }
            }

        verify { driverService.findById(1L) }
    }

    @Test
    fun `deve criar motorista e retornar 201`() {
        val creation = DriverCreation("João", true, CarDto("ABC1234", "Gol", "Preto"))
        val driver = creation.toDriver().apply { id = 1L }
        every { driverService.save(any()) } returns driver.toResponse()

        mockMvc.post("/drivers") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"João","available":true,"car":{"licensePlate":"ABC1234","model":"Gol","color":"Preto"}}"""
        }.andExpect {
            status { isCreated() }
        }

        verify { driverService.save(any()) }
    }

    @Test
    fun `deve atualizar localização do motorista e retornar 200`() {
        val update = DriverLocationUpdate(10.0, 20.0)
        val driver = Driver(1L, "João", true, LocalDateTime.now(), "ABC1234", "Gol", "Preto")
        every { driverService.updateLocation(1L, update) } returns driver.toResponse()

        mockMvc.patch("/drivers/1/location") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"latitude":10.0,"longitude":20.0}"""
        }.andExpect {
            status { isOk() }
        }

        verify { driverService.updateLocation(1L, update) }
    }

    @Test
    fun `deve deletar motorista e retornar 204`() {
        every { driverService.delete(1L) } returns Unit

        mockMvc.delete("/drivers/1")
            .andExpect {
                status { isNoContent() }
            }

        verify { driverService.delete(1L) }
    }
}