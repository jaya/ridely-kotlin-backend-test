package tech.jaya.ridely.model

import jakarta.persistence.*
import org.geolatte.geom.G2D
import org.geolatte.geom.Point
import org.geolatte.geom.builder.DSL.g
import org.geolatte.geom.builder.DSL.point
import org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
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
    @CreationTimestamp
    var activationDate: LocalDateTime,

    @Column(name = "car_license_plate", nullable = false)
    var carLicensePlate: String = "",

    @Column(name = "car_model", nullable = false)
    var carModel: String = "",

    @Column(name = "car_color", nullable = false)
    var carColor: String = "",

    @Column(name = "location_updated_at")
    @CreationTimestamp
    var locationUpdatedAt: LocalDateTime,

    @Column(name = "latitude", nullable = false)
    var latitude: Double,

    @Column(name = "longitude", nullable = false)
    var longitude: Double,

    @Column(name = "location", nullable = false, columnDefinition = "POINT SRID 4326")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    var location: Point<G2D>? = null

) {
    fun becomeBusy() {
        this.available = false
        this.activationDate = LocalDateTime.now()
    }

    fun becomeAvailable() {
        this.available = true
    }

    @PrePersist
    @PreUpdate
    fun fillLocationFromLatLon() {
        val lat = requireNotNull(latitude) { "latitude obrigatória" }
        val lon = requireNotNull(longitude) { "longitude obrigatória" }

        // GeoLatte: point(WGS84, g(x, y))  -> x=lon, y=lat
        location = point(WGS84, g(lon, lat))
        locationUpdatedAt = LocalDateTime.now()
    }



    companion object {
        // Reutilize a GeometryFactory (evita alocar a cada salvamento)
        private val GEOMETRY_FACTORY = GeometryFactory(PrecisionModel(), 4326)
    }
}