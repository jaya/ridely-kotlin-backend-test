package tech.jaya.ridely.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import tech.jaya.ridely.model.Ride
import java.util.Optional

@Repository
interface RideRepo : JpaRepository<Ride, Long> {

    @Query("SELECT r FROM Ride r WHERE r.driver.id=:driverId AND r.status = 'REQUESTED'")
    fun findLastRideByDriveId(driverId: Long): Optional<Ride>

    @Query("SELECT r FROM Ride r WHERE r.passenger.id=:passengerId AND r.status = 'REQUESTED'")
    fun findLastRideByPassengerId(passengerId: Long): Optional<Ride>
}