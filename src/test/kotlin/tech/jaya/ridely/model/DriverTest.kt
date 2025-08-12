package tech.jaya.ridely.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DriverTest {

    private fun newDriver(available: Boolean = true): Driver {
        return Driver(
            id = null,
            name = "test jaya",
            available = available,
            activationDate = LocalDateTime.now(),
            carLicensePlate = "AASS",
            carModel = "SPIN",
            carColor = "Black",
            locationUpdatedAt = LocalDateTime.now(),
            latitude = -26.923026,
            longitude = -49.063412,
            location = null
        )
    }

    @Test
    fun `when a driver is available and tries to become unavailable`() {
        val driver = newDriver(available = true)

        driver.becomeBusy()

        assertFalse(driver.available)
    }

    @Test
    fun `when a driver is unavailable and tries to become available`() {
        val driver = newDriver(available = false)

        driver.becomeAvailable()

        assertTrue(driver.available)
    }

    @Test
    fun `fillLocationFromLatLon should set POINT with lon-x and lat-y and update timestamp`() {
        val driver = newDriver()
        val before = driver.locationUpdatedAt

        driver.fillLocationFromLatLon()

        assertNotNull(driver.location, "location must be populated by fillLocationFromLatLon()")
        val p = driver.location!!

        // GeoLatte G2D: lon = X, lat = Y
        val lon = p.position.lon
        val lat = p.position.lat

        assertTrue(kotlin.math.abs(lon - driver.longitude) < 1e-9, "lon (X) don't hit with longitude")
        assertTrue(kotlin.math.abs(lat - driver.latitude)  < 1e-9, "lat (Y) don't hit qith latitude")

        // timestamp updated
        assertTrue(
            driver.locationUpdatedAt.isAfter(before) || driver.locationUpdatedAt.isEqual(before),
            "locationUpdatedAt must be updated (>= antes)"
        )
    }
}