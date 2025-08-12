package tech.jaya.ridely.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tech.jaya.ridely.controller.dto.CarDto
import tech.jaya.ridely.controller.dto.DriverDto
import tech.jaya.ridely.controller.dto.request.DriverCreationRequest
import tech.jaya.ridely.controller.dto.request.PassengerRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.*
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Status
import tech.jaya.ridely.service.DriverService
import tech.jaya.ridely.service.RideService
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DriverControllerTest {

    @Mock
    lateinit var driverService: DriverService

    @Mock
    lateinit var rideService: RideService

    @InjectMocks
    lateinit var controller: DriverController

    private lateinit var mockMvc: MockMvc
    private val mapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `POST - requestDriver - must return 201 with payload`() {
        val passenger = PassengerRequest(
            name = "Fulano",
            email = ""
        )
        val req = RidelyPayload(
            passenger = passenger,
            pickUp = "R. Teste",
            dropOff = "Teste"
        )

        val car = getCarDto()

        val driver = DriverDto(
            name = "Bruno Souza",
            car = car
        )

        val resp = RequestDriverResponse(
            id = 4,
            driver = driver,
            status = Status.REQUESTED,
            pickUp = "R. Teste",
            dropOff = "Teste"
        )

        whenever(driverService.requestDriver(any())).thenReturn(resp)

        mockMvc.perform(
            post("/drivers/request-driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.status").value("REQUESTED"))
            .andExpect(jsonPath("$.pickUp").value(req.pickUp))
            .andExpect(jsonPath("$.dropOff").value(req.dropOff))
            .andExpect(jsonPath("$.driver.name").value("Bruno Souza"))
            .andExpect(jsonPath("$.driver.car.licensePlate").value("DEF4B56"))
            .andExpect(jsonPath("$.driver.car.model").value("Onix"))
            .andExpect(jsonPath("$.driver.car.color").value("Preto"))
    }

    @Test
    fun `GET - findById - must return 200 with driver response`() {
        val activation = java.time.LocalDateTime.parse("2024-05-30T00:00:00")
        val driverEntity = Driver(
            id = 5L,
            name = "Ana",
            available = true,
            activationDate = activation,
            carLicensePlate = "ABC1A23",
            carModel = "Onix",
            carColor = "Preto",
            locationUpdatedAt = LocalDateTime.now(),
            latitude = -26.923026,
            longitude = -49.063412,
            location = null
        )

        whenever(driverService.findById(5L)).thenReturn(driverEntity)

        mockMvc.perform(get("/drivers/5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("Ana"))
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.activationDate").value(activation.toString()))
            .andExpect(jsonPath("$.car.licensePlate").value("ABC1A23"))
            .andExpect(jsonPath("$.car.model").value("Onix"))
            .andExpect(jsonPath("$.car.color").value("Preto"))
    }

    @Test
    fun `GET - getRide - must return 200 with AcceptResponse`() {
        val passengerResponse = PassengerResponse(
            name = "Fulano",
            email = "fulano@gmail.com"
        )
        val resp = AcceptResponse(
            id = 99L,
            passenger = passengerResponse,
            pickUp = "R. Teste",
            dropOff = "Teste",
            status = Status.REQUESTED,
        )

        whenever(driverService.getRide(anyLong())).thenReturn(resp)

        mockMvc.perform(get("/drivers/7/get-rides"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.status").value("REQUESTED"))
            .andExpect(jsonPath("$.dropOff").value("Teste"))
    }

    private fun getCarDto(): CarDto {
        return CarDto(
            licensePlate = "DEF4B56",
            model = "Onix",
            color = "Preto"
        )
    }

    @Test
    fun `POST - save - must return 201 with driver saved`() {
        val req = DriverCreationRequest(
            name = "Bruno",
            available = true,
            address = "Teste",
            car = getCarDto()
        )

        val driverEntity = Driver(
            id = 12L,
            name = req.name,
            available = req.available,
            activationDate = LocalDateTime.parse("2025-08-12T08:46:26.713101"),
            carLicensePlate = req.car.licensePlate,
            carModel = req.car.model,
            carColor = req.car.color,
            locationUpdatedAt = LocalDateTime.now(),
            latitude = -26.9235,
            longitude = -49.0652,
            location = null
        )

        whenever(driverService.save(any())).thenReturn(driverEntity)

        mockMvc.perform(
            post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.name").value("Bruno"))
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.car.licensePlate").value("DEF4B56"))
            .andExpect(jsonPath("$.car.model").value("Onix"))
            .andExpect(jsonPath("$.car.color").value("Preto"))
    }

    @Test
    fun `DELETE - delete - deve retornar 204`() {
        mockMvc.perform(delete("/drivers/33"))
            .andExpect(status().isNoContent)
    }
}