package tech.jaya.ridely.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.service.PassengerService
import java.time.LocalDateTime

class PassengerControllerTest {

    private val passengerService = mockk<PassengerService>()
    private val controller = PassengerController(passengerService)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    @Test
    fun `deve criar passageiro e retornar 201`() {
        val passenger = Passenger(1L, "Maria", "maria@email.com", false, LocalDateTime.now())
        every { passengerService.save(any()) } returns passenger

        mockMvc.post("/api/passengers") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"Maria","email":"maria@email.com","inTraveling":false,"activationDate":"${passenger.activationDate}"}"""
        }.andExpect {
            status { isCreated() }
        }

        verify { passengerService.save(any()) }
    }

    @Test
    fun `deve buscar passageiro por id e retornar 200`() {
        val passenger = Passenger(1L, "Maria", "maria@email.com", false, LocalDateTime.now())
        every { passengerService.getPassengerById(1L) } returns passenger

        mockMvc.get("/api/passengers/1")
            .andExpect {
                status { isOk() }
            }

        verify { passengerService.getPassengerById(1L) }
    }

    @Test
    fun `deve atualizar passageiro e retornar 200`() {
        val updated = Passenger(1L, "Maria", "maria@email.com", true, LocalDateTime.now())
        every { passengerService.updatePassenger(1L, any()) } returns updated

        mockMvc.put("/api/passengers/1") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"Maria","email":"maria@email.com","inTraveling":true,"activationDate":"${updated.activationDate}"}"""
        }.andExpect {
            status { isOk() }
        }

        verify { passengerService.updatePassenger(1L, any()) }
    }

    @Test
    fun `deve deletar passageiro e retornar 204`() {
        every { passengerService.delete(1L) } returns Unit

        mockMvc.delete("/api/passengers/1")
            .andExpect {
                status { isNoContent() }
            }

        verify { passengerService.delete(1L) }
    }
}