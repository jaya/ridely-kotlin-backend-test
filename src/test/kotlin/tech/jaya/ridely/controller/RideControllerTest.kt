package tech.jaya.ridely.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
import tech.jaya.ridely.controller.dto.NearbyDriverDto
import tech.jaya.ridely.controller.dto.request.FinishRideRequest
import tech.jaya.ridely.controller.dto.request.PassengerRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.*
import tech.jaya.ridely.model.Status
import tech.jaya.ridely.service.RideService
import tech.jaya.ridely.service.dto.RideResponseDto
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class RideControllerTest {

    @Mock
    lateinit var rideService: RideService

    @InjectMocks
    lateinit var controller: RideController

    private lateinit var mockMvc: MockMvc
    private val mapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `PATCH - refuseRide - must return 200 with payload`() {
        val passengerResponse = PassengerResponse(
            name = "Fulano",
            email = "fulano@gmail.com"
        )
        val resp = RefuseResponse(
            id = 10L,
            passenger = passengerResponse,
            pickUp = "R. Teste",
            dropOff = "Teste",
            status = Status.REFUSED
        )
        whenever(rideService.refuseRide(10L)).thenReturn(resp)

        mockMvc.perform(patch("/rides/10/refuse-ride"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.status").value("REFUSED"))
    }

    @Test
    fun `PATCH - cancel-ride - must return 200 with payload`() {
        val resp = CancelResponse(
            id = 11L,
            pickUp = "R. Teste",
            dropOff = "Teste",
            status = Status.CANCELLED
        )
        whenever(rideService.deleteRide(11L)).thenReturn(resp)

        mockMvc.perform(patch("/rides/11/cancel-ride"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(11))
            .andExpect(jsonPath("$.status").value("CANCELLED"))
    }

    @Test
    fun `PATCH - finish-ride - must returnr 200 with payload`() {
        val passengerResponse = PassengerResponse(
            name = "Fulano",
            email = "fulano@gmail.com"
        )

        val req = FinishRideRequest(price = BigDecimal("27.90"))

        val resp = FinishResponse(
            id = 12L,
            passenger = passengerResponse,
            dropOff = "Teste",
            status = Status.COMPLETED,
            price = BigDecimal("27.90")
        )
        whenever(rideService.finishRide(12L, req)).thenReturn(resp)

        mockMvc.perform(
            patch("/rides/12/finish-ride")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.price").value(27.90))
    }

    @Test
    fun `PATCH - accept-ride - must return 200 with payload`() {
        val passengerResponse = PassengerResponse(
            name = "Fulano",
            email = "fulano@gmail.com"
        )

        val resp = AcceptResponse(
            id = 13,
            passenger = passengerResponse,
            pickUp = "R. Teste",
            dropOff = "Teste",
            status = Status.IN_PROGRESS,
        )
        whenever(rideService.acceptRide(13L)).thenReturn(resp)

        mockMvc.perform(patch("/rides/13/accept-ride"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(13))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
    }

    @Test
    fun `DELETE - delete - deve retornar 204`() {
        mockMvc.perform(delete("/rides/20"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `POST - request-ride - deve retornar 200 com quote`() {
        val passenger = PassengerRequest(
            name = "Fulano",
            email = ""
        )
        val req = RidelyPayload(
            passenger = passenger,
            pickUp = "R. Teste",
            dropOff = "Teste"
        )

        val quote = RideResponseDto(
            estimedTimeMinutes = 12,
            kmDistance = 3.40,
            estimedPrice = BigDecimal("27.90"),
            nearbyDrivers = listOf(
                NearbyDriverDto(driverId = 6, name = "Paulo"),
                NearbyDriverDto(driverId = 7, name = "Rita"),
                NearbyDriverDto(driverId = 1, name = "Ana")
            )
        )

        whenever(rideService.requestRide(any())).thenReturn(quote)

        mockMvc.perform(
            post("/rides/request-ride")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.tempo_estimado_minutos").value(12))
            .andExpect(jsonPath("$.distancia_km").value(3.40))
            .andExpect(jsonPath("$.preco_estimado").value(27.90))
            .andExpect(jsonPath("$.nearbyDrivers[0].driverId").value(6))
            .andExpect(jsonPath("$.nearbyDrivers[0].name").value("Paulo"))
            .andExpect(jsonPath("$.nearbyDrivers[1].driverId").value(7))
            .andExpect(jsonPath("$.nearbyDrivers[2].driverId").value(1))
    }
}