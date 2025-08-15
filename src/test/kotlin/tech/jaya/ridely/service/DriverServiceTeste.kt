package tech.jaya.ridely.service

import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import tech.jaya.ridely.dtos.*
import tech.jaya.ridely.exception.DriverNotFound
import tech.jaya.ridely.exception.RideNotFoundException
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.model.Ride
import tech.jaya.ridely.model.Status
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class DriverServiceTest {

    private val driverRepo = mockk<DriverRepo>()
    private val rideRepo = mockk<RideRepo>()
    private val geocodingService = mockk<GoogleGeocodingService>()
    private lateinit var driverService: DriverService

    @BeforeEach
    fun setUp() {
        driverService = DriverService(driverRepo, rideRepo, geocodingService)
    }

    @Test
    fun `deve buscar motorista por id`() {
        val driver = Driver(1L, "João", true, LocalDateTime.now(), "ABC1234", "Gol", "Preto")
        every { driverRepo.findById(1L) } returns Optional.of(driver)
        val resp = driverService.findById(1L)
        assertEquals("João", resp.name)
        verify { driverRepo.findById(1L) }
    }

    @Test
    fun `deve lançar DriverNotFound se motorista não existir`() {
        every { driverRepo.findById(2L) } returns Optional.empty()
        assertThrows<DriverNotFound> { driverService.findById(2L) }
    }

    @Test
    fun `deve encontrar motorista mais próximo por coordenada`() {
        val driver1 = Driver(1L, "A", true, LocalDateTime.now(), "A", "A", "A", latitude = 0.0, longitude = 0.0)
        val driver2 = Driver(2L, "B", true, LocalDateTime.now(), "B", "B", "B", latitude = 1.0, longitude = 1.0)
        every { driverRepo.findAll() } returns listOf(driver1, driver2)
        val result = driverService.findNearestDriver(0.1, 0.1)
        assertEquals(driver1, result)
    }

    @Test
    fun `deve retornar null se não houver motoristas disponíveis`() {
        every { driverRepo.findAll() } returns emptyList()
        val result = driverService.findNearestDriver(0.0, 0.0)
        assertNull(result)
    }

    @Test
    fun `deve calcular distância haversine corretamente`() {
        val dist = driverService.haversine(0.0, 0.0, 0.0, 1.0)
        assertTrue(dist > 100.0) // Aproximadamente 111 km
    }

    @Test
    fun `deve encontrar motorista mais próximo por cidade e sublocalidade`() {
        val driver1 = Driver(1L, "A", true, LocalDateTime.now(), "A", "A", "A", latitude = 0.0, longitude = 0.0)
        val driver2 = Driver(2L, "B", true, LocalDateTime.now(), "B", "B", "B", latitude = 1.0, longitude = 1.0)
        every { driverRepo.findByCityAndSublocality("Cidade", "Bairro") } returns listOf(driver1, driver2)
        val result = driverService.findNearestDriverByLocationInfo("Cidade", "Bairro", 0.1, 0.1)
        assertEquals(driver1, result)
    }

    @Test
    fun `deve atualizar localização do motorista`() {
        val driver = Driver(1L, "João", true, LocalDateTime.now(), "ABC", "Gol", "Preto")
        val update = DriverLocationUpdate(10.0, 20.0)
        every { driverRepo.findById(1L) } returns Optional.of(driver)
        every { geocodingService.getLocationInfo(10.0, 20.0) } returns LocationInfo("Bairro", "Cidade")
        every { driverRepo.save(any()) } returns driver
        val resp = driverService.updateLocation(1L, update)
        assertEquals(10.0, driver.latitude)
        assertEquals("Cidade", driver.city)
        assertEquals("Bairro", driver.sublocality)
        verify { driverRepo.save(driver) }
    }

    @Test
    fun `deve lançar DriverNotFound ao atualizar localização de motorista inexistente`() {
        every { driverRepo.findById(2L) } returns Optional.empty()
        assertThrows<DriverNotFound> {
            driverService.updateLocation(2L, DriverLocationUpdate(1.0, 1.0))
        }
    }

    @Test
    fun `deve buscar última corrida do motorista`() {
        val passenger = Passenger(1L, "João", "joao@email.com")
        val driver = Driver(1L, "Maria", true, LocalDateTime.now(), "ABC", "Gol", "Preto")
        val ride = Ride(1L, "A", "B", Status.IN_PROGRESS, driver, passenger, 10, 20, BigDecimal.TEN)
        every { rideRepo.findLastRideByDriveId(1L) } returns Optional.of(ride)
        val resp = driverService.getLastRide(1L)
        assertEquals(ride.id, resp.id)
    }

    @Test
    fun `deve lançar RideNotFoundException se motorista não tiver corridas`() {
        every { rideRepo.findLastRideByDriveId(1L) } returns Optional.empty()
        assertThrows<RideNotFoundException> { driverService.getLastRide(1L) }
    }

    @Test
    fun `deve salvar novo motorista`() {
        val creation = DriverCreation("João", true, CarDto("ABC", "Gol", "Preto"))
        val driver = creation.toDriver()
        driver.id = 1L
        every { driverRepo.save(any()) } returns driver
        val resp = driverService.save(creation)
        assertEquals("João", resp.name)
        verify { driverRepo.save(any()) }
    }

    @Test
    fun `deve salvar alterações do motorista`() {
        val driver = Driver(1L, "João", true, LocalDateTime.now(), "ABC", "Gol", "Preto")
        every { driverRepo.save(driver) } returns driver
        val result = driverService.saveDriver(driver)
        assertEquals(driver, result)
        verify { driverRepo.save(driver) }
    }

    @Test
    fun `deve deletar motorista`() {
        every { driverRepo.deleteById(1L) } just Runs
        driverService.delete(1L)
        verify { driverRepo.deleteById(1L) }
    }
}