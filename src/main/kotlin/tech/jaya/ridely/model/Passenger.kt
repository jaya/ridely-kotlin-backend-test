package tech.jaya.ridely.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "passenger")
class Passenger(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "email", nullable = false)
    var email: String = "",

    @Column(name = "in_Traveling", nullable = false)
    var inTraveling : Boolean = false,

    @Column(name = "activation_date", nullable = false)
    var activationDate: LocalDateTime = LocalDateTime.now(),
) {
    fun becomeTraveling() {
        this.inTraveling  = true
        this.activationDate = LocalDateTime.now()
    }

    fun exitTravel() {
        this.inTraveling  = false
    }
}