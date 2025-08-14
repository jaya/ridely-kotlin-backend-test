package tech.jaya.ridely.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * This data class represents a Driver entity.
 *
 * @property id the unique identifier of the driver. It's nullable to allow the creation of a driver without an id.
 * @property name the name of the driver.
 * @property available a boolean indicating whether the driver is available or not.
 * @property carLicensePlate the license plate associated with the driver.
 * @property carModel the model of the car associated with the driver.
 * @property carColor the color of the car associated with the driver.
 * @property activationDate the date and time when the driver became busy.
 */

@Entity
@Table(name = "driver")
class Driver(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "available", nullable = false)
    var available: Boolean = true,

    @Column(name = "activation_date", nullable = false)
    var activationDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "car_license_plate", nullable = false)
    var carLicensePlate: String = "",

    @Column(name = "car_model", nullable = false)
    var carModel: String = "",

    @Column(name = "car_color", nullable = false)
    var carColor: String = "",

    @Column(name = "latitude", nullable = false)
    var latitude: Double = 0.0,

    @Column(name = "longitude", nullable = false)
    var longitude: Double = 0.0,

    @Column(name = "city", nullable = false)
    var city: String = "",

    @Column(name = "sublocality", nullable = false)
    var sublocality: String = ""

) {
    fun becomeBusy() {
        this.available = false
        this.activationDate = LocalDateTime.now()
    }

    fun becomeAvailable() {
        this.available = true
    }
}