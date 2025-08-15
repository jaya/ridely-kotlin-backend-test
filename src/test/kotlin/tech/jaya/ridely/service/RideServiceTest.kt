import io.mockk.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.assertThrows
import tech.jaya.ridely.dtos.LocationInfo
import tech.jaya.ridely.dtos.RequestDriver
import tech.jaya.ridely.dtos.RouteInfo
import tech.jaya.ridely.exception.DriverUnavailable
import tech.jaya.ridely.exception.RideInvalidState
import tech.jaya.ridely.model.*
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.service.*
import org.junit.jupiter.api.Assertions.*
import tech.jaya.ridely.dtos.*
import tech.jaya.ridely.exception.RideNotFoundException
import java.math.BigDecimal
import java.util.*

class RideServiceTest {

    private val passengerService = mockk<PassengerService>()
    private val googleMapsService = mockk<GoogleMapsService>()
    private val googleGeocodingService = mockk<GoogleGeocodingService>()
    private val driverService = mockk<DriverService>()
    private val rideRepo = mockk<RideRepo>(relaxed = true) // relaxed evita precisar mockar save()

    private lateinit var rideService: RideService

    @BeforeEach
    fun setUp() {
        rideService = RideService(
            rideRepo,
            googleMapsService,
            googleGeocodingService,
            driverService,
            passengerService
        )
    }

    @Test
    fun `deve criar corrida com sucesso`() {
        val req = RequestDriver(
            passengerId = 1L,
            pickUp = "Local A",
            dropOff = "Local B"
        )

        val passenger = Passenger(id = 1L, name = "João")
        val driver = Driver(id = 10L, name = "Maria")

        every { passengerService.getPassengerById(1L) } returns passenger
        every { googleMapsService.getRouteInfo("Local A", "Local B") } returns RouteInfo(
            distance = 1000,
            duration = 600,
            startLat = -7.115,
            startLng = -34.864
        )
        every { googleGeocodingService.getLocationInfo(any(), any()) } returns LocationInfo(
            city = "João Pessoa",
            sublocality = "Centro"
        )
        every { driverService.findNearestDriverByLocationInfo(any(), any(), any(), any()) } returns driver
        every { driverService.saveDriver(any()) } answers { firstArg<Driver>() }
        every { rideRepo.save(any()) } answers { firstArg<Ride>() }
        every { passengerService.save(any()) } answers { firstArg<Passenger>() }

        rideService.requestDriver(req)

        verify { rideRepo.save(any()) }
        verify { driverService.saveDriver(driver) }
        verify { passengerService.save(passenger) }
    }

    @Test
    fun `deve lançar RideInvalidState quando rota for inválida`() {
        val req = RequestDriver(1L, "Local A", "Local B")
        every { passengerService.getPassengerById(1L) } returns Passenger(1L, "João")
        every { googleMapsService.getRouteInfo(any(), any()) } returns RouteInfo(0, 0, 0.0, 0.0)

        assertThrows<RideInvalidState> {
            rideService.requestDriver(req)
        }
    }

    @Test
    fun `deve lançar DriverUnavailable quando não encontrar motorista`() {
        val req = RequestDriver(1L, "Local A", "Local B")
        every { passengerService.getPassengerById(1L) } returns Passenger(1L, "João")
        every { googleMapsService.getRouteInfo(any(), any()) } returns RouteInfo(
            1000, 600, -7.115, -34.864
        )
        every { googleGeocodingService.getLocationInfo(any(), any()) } returns LocationInfo(
            city = "João Pessoa",
            sublocality = "Centro"
        )
        every { driverService.findNearestDriverByLocationInfo(any(), any(), any(), any()) } returns null
        every { driverService.findNearestDriver(any(), any()) } returns null

        assertThrows<DriverUnavailable> {
            rideService.requestDriver(req)
        }
    }

    @Test
    fun `deve converter metros para quilometros arredondando para cima`() {
        assertEquals(1, rideService.metersToKilometers(1))
        assertEquals(1, rideService.metersToKilometers(999))
        assertEquals(2, rideService.metersToKilometers(1001))
        assertEquals(5, rideService.metersToKilometers(4001))
    }

    @Test
    fun `deve converter segundos para minutos arredondando para cima`() {
        assertEquals(1, rideService.secondsToMinutes(1))
        assertEquals(1, rideService.secondsToMinutes(59))
        assertEquals(2, rideService.secondsToMinutes(61))
        assertEquals(5, rideService.secondsToMinutes(241))
    }

    @Test
    fun `deve calcular o preco da corrida corretamente`() {
        val price = rideService.calculateRidePrice(10, 20)
        assertEquals(BigDecimal("70.70"), price)
    }

    @Test
    fun `deve buscar a ultima corrida por passageiro`() {
        val driver = mockk<Driver>(relaxed = true)
        every { driver.name } returns "Maria"
        val ride = Ride(1, "A", "B", Status.REQUESTED, driver, Passenger(1, "João"), 10, 20, BigDecimal.TEN)
        every { rideRepo.findLastRideByPassengerId(1) } returns Optional.of(ride)
        val result = rideService.findLastRideByPassengerId(1)
        assertNotNull(result)
        verify { rideRepo.findLastRideByPassengerId(1) }
    }

    @Test
    fun `deve retornar null se nao houver corrida para passageiro`() {
        every { rideRepo.findLastRideByPassengerId(1) } returns Optional.empty()
        val result = rideService.findLastRideByPassengerId(1)
        assertNull(result)
    }

    @Test
    fun `deve recusar corrida`() {
        val ride = spyk(Ride(1, "A", "B", Status.REQUESTED, mockk(relaxed = true), Passenger(1, "João"), 10, 20, BigDecimal.TEN))
        every { rideRepo.findById(1) } returns Optional.of(ride)
        every { rideRepo.save(any()) } returns ride
        val req = ActionRideRequest(1)
        val response = rideService.refuseRide(req)
        assertNotNull(response)
        verify { rideRepo.save(ride) }
    }

    @Test
    fun `deve cancelar corrida`() {
        val ride = spyk(Ride(1, "A", "B", Status.REQUESTED, mockk(relaxed = true), Passenger(1, "João"), 10, 20, BigDecimal.TEN))
        every { rideRepo.findById(1) } returns Optional.of(ride)
        every { rideRepo.save(any()) } returns ride
        val req = ActionRideRequest(1)
        val response = rideService.cancelRide(req)
        assertNotNull(response)
        verify { rideRepo.save(ride) }
    }

    @Test
    fun `deve finalizar corrida`() {
        val ride = spyk(Ride(1, "A", "B", Status.IN_PROGRESS, mockk(relaxed = true), Passenger(1, "João"), 10, 20, BigDecimal.TEN))
        every { rideRepo.findById(1) } returns Optional.of(ride)
        every { rideRepo.save(any()) } returns ride
        val req = FinishRideRequest(1, BigDecimal("20.00"))
        val response = rideService.finishRide(req)
        assertNotNull(response)
        verify { rideRepo.save(ride) }
    }

    @Test
    fun `deve aceitar corrida`() {
        val ride = spyk(Ride(1, "A", "B", Status.REQUESTED, mockk(relaxed = true), Passenger(1, "João"), 10, 20, BigDecimal.TEN))
        every { rideRepo.findById(1) } returns Optional.of(ride)
        every { rideRepo.save(any()) } returns ride
        val req = ActionRideRequest(1)
        val response = rideService.acceptRide(req)
        assertNotNull(response)
        verify { rideRepo.save(ride) }
    }

    @Test
    fun `deve deletar corrida`() {
        every { rideRepo.deleteById(1) } just Runs
        rideService.delete(1)
        verify { rideRepo.deleteById(1) }
    }

    @Test
    fun `deve lançar RideNotFoundException ao recusar corrida inexistente`() {
        every { rideRepo.findById(99) } returns Optional.empty()
        assertThrows<RideNotFoundException> {
            rideService.refuseRide(ActionRideRequest(99))
        }
    }
}
