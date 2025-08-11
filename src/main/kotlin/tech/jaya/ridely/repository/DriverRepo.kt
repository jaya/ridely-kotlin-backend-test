package tech.jaya.ridely.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import tech.jaya.ridely.model.Driver
import java.time.LocalDateTime
import java.util.*

@Repository
interface DriverRepo : JpaRepository<Driver, Long> {
    @Query("SELECT e FROM Driver e WHERE e.available=true order by e.activationDate asc limit 1")
    fun findAvailableDriver(): Optional<Driver>

//    @Query(
//        value = """
//              INSERT INTO driver (
//              name, activation_date, car_license_plate, car_model, car_color, available,
//              latitude, longitude, location_updated_at, location
//            ) VALUES (
//              :name, NOW(), :car_license_plate, :car_model, :car_color, :available,
//              :latitude, :longitude, NOW(),
//              ST_SRID(POINT(:longitude,  :latitude), 4326)
//            )
//""",
//        nativeQuery = true
//)
//    fun createDriver(
//        @Param("name") name: String,
//        @Param("activation_date") activationDate: LocalDateTime,
//        @Param("car_license_plate") carLicensePlate: String,
//        @Param("car_model") carModel: String,
//        @Param("car_color") carColor: String,
//        @Param("available") available: Boolean,
//        @Param("location_updated_at") locationUpdatedAt: LocalDateTime,
//        @Param("latitude") latitude: Double,
//        @Param("longitude") longitude: Double
//    ):Driver

    @Query(
        value = """
            SELECT
    d.*,
    ST_Distance_Sphere(
            d.location,
            ST_GeomFromText(CONCAT('POINT(', r.origin_lon, ' ', r.origin_lat, ')'), 4326)
    ) AS distance_m
FROM driver d
         JOIN ride r ON r.id = :rideId
WHERE d.available = 1
 -- AND d.location_updated_at > NOW() - INTERVAL 5 MINUTE
  AND NOT ST_Equals(d.location, ST_GeomFromText('POINT(0 0)', 4326))
ORDER BY distance_m
LIMIT 3;
        """,
        nativeQuery = true
    )
    fun findNearestByRide(rideId: Long): List<Driver>
}