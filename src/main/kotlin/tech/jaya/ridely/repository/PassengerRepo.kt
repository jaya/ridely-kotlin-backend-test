package tech.jaya.ridely.repository

import org.springframework.data.jpa.repository.JpaRepository
import tech.jaya.ridely.model.Passenger

interface PassengerRepo: JpaRepository<Passenger, Long>