package tech.jaya.ridely.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tech.jaya.ridely.controller.exception.RideInvalidState
import java.math.BigDecimal
import java.time.LocalDateTime

class RideTest {

    private fun newDriver(available: Boolean = true): Driver {
        return Driver(
            id = null,
            name = "Motorista Teste",
            available = available,
            activationDate = LocalDateTime.now(),
            carLicensePlate = "ABC1D23",
            carModel = "Onix",
            carColor = "Preto",
            locationUpdatedAt = LocalDateTime.now(),
            latitude = -26.923026,
            longitude = -49.063412,
            location = null
        )
    }

    private fun newRide(
        status: Status = Status.REQUESTED,
        driver: Driver? = null
    ): Ride {
        return Ride(
            id = null,
            pickUp = "Rua A, 100",
            dropOff = "Av B, 200",
            status = status,
            driver = driver,
            passengerName = "Passageiro Teste",
            passengerEmail = "passageiro@teste.com",
            originLat = -26.924000,
            originLon = -49.065000,
            price = BigDecimal.ZERO
        )
    }

    // ---------- request() ----------
    @Test
    fun `request must associate driver and mark as REQUESTED and leave driver busy`() {
        val driver = newDriver(available = true)
        val ride = newRide(status = Status.REFUSED, driver = null)

        ride.request(driver)

        assertEquals(Status.REQUESTED, ride.status)
        assertSame(driver, ride.driver)
        assertFalse(driver.available, "driver should be busy afterrequest()")
    }

    @Test
    fun `request must fail if running is COMPLETED`() {
        val ride = newRide(status = Status.COMPLETED)
        val driver = newDriver()

        assertThrows<RideInvalidState> { ride.request(driver) }
    }

    // ---------- cancel() ----------
    @Test
    fun `cancel must release driver and mark CANCELLED`() {
        val driver = newDriver(available = true)
        val ride = newRide(status = Status.REQUESTED, driver = driver)

        driver.becomeBusy()

        ride.cancel()

        assertEquals(Status.CANCELLED, ride.status)
        assertTrue(driver.available, "driver should be available after cancel()")
    }

    @Test
    fun `cancel must fail if running is COMPLETED`() {
        val ride = newRide(status = Status.COMPLETED, driver = newDriver())
        assertThrows<RideInvalidState> { ride.cancel() }
    }

    @Test
    fun `cancel must fail if there is no driver`() {
        val ride = newRide(status = Status.REQUESTED, driver = null)
        assertThrows<RideInvalidState> { ride.cancel() }
    }

    // ---------- accept() ----------
    @Test
    fun `accept must change REQUESTED to IN_PROGRESS and leave driver busy`() {
        val driver = newDriver(available = true)
        val ride = newRide(status = Status.REQUESTED, driver = driver)

        ride.accept()

        assertEquals(Status.IN_PROGRESS, ride.status)
        assertFalse(driver.available, "driver should be busy after accept()")
    }

    @Test
    fun `accept must fail if status not for REQUESTED`() {
        val ride = newRide(status = Status.CANCELLED, driver = newDriver())
        assertThrows<RideInvalidState> { ride.accept() }
    }

    @Test
    fun `accept must fail if there is no driver`() {
        val ride = newRide(status = Status.REQUESTED, driver = null)
        assertThrows<RideInvalidState> { ride.accept() }
    }

    // ---------- complete() ----------
    @Test
    fun `complete must change to completed point and release driver`() {
        val driver = newDriver(available = false)
        val ride = newRide(status = Status.IN_PROGRESS, driver = driver)

        ride.complete(BigDecimal("27.90"))

        assertEquals(Status.COMPLETED, ride.status)
        assertEquals(BigDecimal("27.90"), ride.price)
        assertTrue(driver.available, "driver should be available after complete()")
    }

    @Test
    fun `complete must fail if status not for IN_PROGRESS`() {
        val ride = newRide(status = Status.REQUESTED, driver = newDriver(available = false))
        assertThrows<RideInvalidState> { ride.complete(BigDecimal.TEN) }
    }

    @Test
    fun `complete must fail if there is no driver`() {
        val ride = newRide(status = Status.IN_PROGRESS, driver = null)
        assertThrows<RideInvalidState> { ride.complete(BigDecimal.TEN) }
    }

    // ---------- refuse() ----------
    @Test
    fun `refuse you must mark REFUSED and release driver`() {
        val driver = newDriver(available = false)
        val ride = newRide(status = Status.REQUESTED, driver = driver)

        ride.refuse()

        assertEquals(Status.REFUSED, ride.status)
        assertTrue(driver.available, "driver should be available after refuse()")
    }

    @Test
    fun `refuse must fail if running is COMPLETED`() {
        val ride = newRide(status = Status.COMPLETED, driver = newDriver(available = false))
        assertThrows<RideInvalidState> { ride.refuse() }
    }

    @Test
    fun `refuse must fail if there is no driver`() {
        val ride = newRide(status = Status.REQUESTED, driver = null)
        assertThrows<RideInvalidState> { ride.refuse() }
    }
}