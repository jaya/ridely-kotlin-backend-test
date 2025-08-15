package tech.jaya.ridely.service

import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.repository.PassengerRepo
import tech.jaya.ridely.exception.PassengerNotFoundException
import tech.jaya.ridely.exception.PassengerUnavailable
import java.time.LocalDateTime
import java.util.*

class PassengerServiceTest {

    private val passengerRepo = mockk<PassengerRepo>()
    private lateinit var passengerService: PassengerService

    @BeforeEach
    fun setUp() {
        passengerService = PassengerService(passengerRepo)
    }

    @Test
    fun `deve salvar passageiro`() {
        val passenger = Passenger(1L, "Maria", "maria@email.com", false, LocalDateTime.now())
        every { passengerRepo.save(passenger) } returns passenger
        val result = passengerService.save(passenger)
        assertEquals(passenger, result)
        verify { passengerRepo.save(passenger) }
    }

    @Test
    fun `deve deletar passageiro`() {
        every { passengerRepo.deleteById(1L) } just Runs
        passengerService.delete(1L)
        verify { passengerRepo.deleteById(1L) }
    }

    @Test
    fun `deve atualizar passageiro existente`() {
        val oldPassenger = Passenger(1L, "Maria", "maria@email.com", false, LocalDateTime.now())
        val updated = Passenger(1L, "Ana", "ana@email.com", true, LocalDateTime.now())
        every { passengerRepo.findById(1L) } returns Optional.of(oldPassenger)
        every { passengerRepo.save(any()) } returns updated
        val result = passengerService.updatePassenger(1L, updated)
        assertEquals("Ana", result.name)
        assertEquals("ana@email.com", result.email)
        assertTrue(result.inTraveling)
        verify { passengerRepo.save(oldPassenger) }
    }

    @Test
    fun `deve lançar PassengerNotFoundException ao atualizar passageiro inexistente`() {
        val updated = Passenger(1L, "Ana", "ana@email.com", true, LocalDateTime.now())
        every { passengerRepo.findById(1L) } returns Optional.empty()
        assertThrows<PassengerNotFoundException> {
            passengerService.updatePassenger(1L, updated)
        }
    }

    @Test
    fun `deve buscar passageiro por id`() {
        val passenger = Passenger(1L, "Maria", "maria@email.com", false, LocalDateTime.now())
        every { passengerRepo.findById(1L) } returns Optional.of(passenger)
        val result = passengerService.getPassengerById(1L)
        assertEquals(passenger, result)
        verify { passengerRepo.findById(1L) }
    }

    @Test
    fun `deve lançar PassengerNotFoundException ao buscar passageiro inexistente`() {
        every { passengerRepo.findById(2L) } returns Optional.empty()
        assertThrows<PassengerNotFoundException> {
            passengerService.getPassengerById(2L)
        }
    }

    @Test
    fun `deve lançar PassengerUnavailable se passageiro estiver em viagem`() {
        val passenger = Passenger(1L, "Maria", "maria@email.com", true, LocalDateTime.now())
        every { passengerRepo.findById(1L) } returns Optional.of(passenger)
        assertThrows<PassengerUnavailable> {
            passengerService.getPassengerById(1L)
        }
    }
}