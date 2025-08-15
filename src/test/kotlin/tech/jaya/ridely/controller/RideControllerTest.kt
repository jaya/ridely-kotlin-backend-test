package tech.jaya.ridely.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.ActionRideRequest
import tech.jaya.ridely.dtos.CancelResponse
import tech.jaya.ridely.dtos.FinishResponse
import tech.jaya.ridely.dtos.FinishRideRequest
import tech.jaya.ridely.dtos.RefuseResponse
import tech.jaya.ridely.dtos.RequestDriver
import tech.jaya.ridely.exception.PassengerNotFoundException
import tech.jaya.ridely.exception.PassengerUnavailable
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.service.PassengerService
import tech.jaya.ridely.service.RideProducer
import tech.jaya.ridely.service.RideService
import java.math.BigDecimal

class RideControllerTest {

    private val passengerService = mockk<PassengerService>()
    private val rideService = mockk<RideService>()
    private val rideProducer = mockk<RideProducer>(relaxed = true) // relaxed evita precisar mockar voids
    private val controller = RideController( rideService, rideProducer, passengerService )

    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    @Test
    fun `deve retornar 202 quando passageiro existe`() {
        val req = RequestDriver(passengerId = 1, pickUp = "A", dropOff = "B")
        every { passengerService.getPassengerById(1) } returns Passenger(1, "John Doe", "123456")

        mockMvc.post("/rides/request-driver") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"passengerId": 1, "pickUp": "A", "dropOff": "B"}
            """
        }.andExpect {
            status { isAccepted() }
            content { string("Ride request sent for processing") }
        }

        verify { rideProducer.sendRideRequest(req) }
    }

    @Test
    fun `deve retornar 400 quando passageiro nao encontrado`() {
        every { passengerService.getPassengerById(1) } throws PassengerNotFoundException("not found")

        mockMvc.post("/rides/request-driver") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"passengerId": 1, "pickUp": "A", "dropOff": "B"}
            """
        }.andExpect {
            status { isBadRequest() }
            content { string("Passenger not found") }
        }
    }

    @Test
    fun `deve retornar 400 quando passageiro ja estiver em corrida`() {
        every { passengerService.getPassengerById(1) } throws PassengerUnavailable("already in ride")

        mockMvc.post("/rides/request-driver") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"passengerId": 1, "pickUp": "A", "dropOff": "B"}
            """
        }.andExpect {
            status { isBadRequest() }
            content { string("Passenger is already in a ride") }
        }
    }

    @Test
    fun `deve recusar corrida e retornar 200`() {
        val passenger = Passenger(1, "John Doe", "john@doe.com")
        val ride = tech.jaya.ridely.model.Ride(
            id = 1,
            pickUp = "A",
            dropOff = "B",
            status = tech.jaya.ridely.model.Status.REFUSED,
            driver = mockk(relaxed = true),
            passenger = passenger,
            distance = 10,
            duration = 20,
            price = BigDecimal.TEN
        )
        val response = RefuseResponse.fromRide(ride)
        val req = ActionRideRequest(1)
        every { rideService.refuseRide(req) } returns response

        mockMvc.post("/rides/refuse-ride") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"id": 1}"""
        }.andExpect {
            status { isOk() }
        }

        verify { rideService.refuseRide(req) }
    }

    @Test
    fun `deve cancelar corrida e retornar 200`() {
        val passenger = Passenger(1, "John Doe", "john@doe.com")
        val ride = tech.jaya.ridely.model.Ride(
            id = 1,
            pickUp = "A",
            dropOff = "B",
            status = tech.jaya.ridely.model.Status.CANCELLED,
            driver = mockk(relaxed = true),
            passenger = passenger,
            distance = 10,
            duration = 20,
            price = BigDecimal.TEN
        )
        val response = CancelResponse.fromRide(ride)
        val req = ActionRideRequest(1)
        every { rideService.cancelRide(req) } returns response

        mockMvc.post("/rides/cancel-ride") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"id": 1}"""
        }.andExpect {
            status { isOk() }
        }

        verify { rideService.cancelRide(req) }
    }

    @Test
    fun `deve finalizar corrida e retornar 200`() {
        val passenger = Passenger(1, "John Doe", "john@doe.com")
        val ride = tech.jaya.ridely.model.Ride(
            id = 1,
            pickUp = "A",
            dropOff = "B",
            status = tech.jaya.ridely.model.Status.COMPLETED,
            driver = mockk(relaxed = true),
            passenger = passenger,
            distance = 10,
            duration = 20,
            price = BigDecimal.TEN
        )
        val response = FinishResponse.fromRide(ride)
        val req = FinishRideRequest(1, BigDecimal.TEN)
        every { rideService.finishRide(req) } returns response

        mockMvc.post("/rides/finish-ride") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"id": 1, "price": 10}"""
        }.andExpect {
            status { isOk() }
        }

        verify { rideService.finishRide(req) }
    }

    @Test
    fun `deve aceitar corrida e retornar 200`() {
        val passenger = Passenger(1, "John Doe", "john@doe.com")
        val ride = tech.jaya.ridely.model.Ride(
            id = 1,
            pickUp = "A",
            dropOff = "B",
            status = tech.jaya.ridely.model.Status.IN_PROGRESS,
            driver = mockk(relaxed = true),
            passenger = passenger,
            distance = 10,
            duration = 20,
            price = BigDecimal.TEN
        )
        val response = AcceptResponse.fromRide(ride)
        val req = ActionRideRequest(1)
        every { rideService.acceptRide(req) } returns response

        mockMvc.post("/rides/accept-ride") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"id": 1}"""
        }.andExpect {
            status { isOk() }
        }

        verify { rideService.acceptRide(req) }
    }

    @Test
    fun `deve deletar corrida e retornar 204`() {
        every { rideService.delete(1) } returns Unit

        mockMvc.delete("/rides/1")
            .andExpect {
                status { isNoContent() }
            }

        verify { rideService.delete(1) }
    }

}
