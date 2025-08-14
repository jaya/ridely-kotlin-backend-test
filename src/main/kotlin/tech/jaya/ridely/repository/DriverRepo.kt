package tech.jaya.ridely.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import tech.jaya.ridely.model.Driver
import java.util.Optional

@Repository
interface DriverRepo : JpaRepository<Driver, Long> {
    @Query("SELECT e FROM Driver e WHERE e.available=true order by e.activationDate asc limit 1")
    fun findAvailableDriver(): Optional<Driver>

    @Query("SELECT d FROM Driver d WHERE d.city = :city AND d.sublocality = :sublocality AND d.available = true")
    fun findByCityAndSublocality(city: String, sublocality: String): List<Driver>
}