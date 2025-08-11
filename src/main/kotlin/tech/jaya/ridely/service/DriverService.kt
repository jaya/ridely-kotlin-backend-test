package tech.jaya.ridely.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tech.jaya.ridely.controller.dto.request.DriverCreationRequest
import tech.jaya.ridely.controller.dto.request.RidelyPayload
import tech.jaya.ridely.controller.dto.response.AcceptResponse
import tech.jaya.ridely.controller.dto.response.RequestDriverResponse
import tech.jaya.ridely.controller.exception.DriverNotFoundException
import tech.jaya.ridely.controller.exception.DriverUnavailable
import tech.jaya.ridely.controller.exception.RideNotFoundException
import tech.jaya.ridely.integration.googlemaps.GoogleMapsFeignClient
import tech.jaya.ridely.integration.googlemaps.dto.geocode.GoogleMapsApiGeocodeResponse
import tech.jaya.ridely.model.Driver
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo

@Service
class DriverService(
    private val driverRepo: DriverRepo,
    private val rideRepo: RideRepo,
    private val googleMapsFeignClient: GoogleMapsFeignClient
) {

    @Value("\${google.maps.api.key}")
    private val KEY: String = ""

    fun requestDriver(req: RidelyPayload): RequestDriverResponse {
        val driver = driverRepo.findAvailableDriver().orElseThrow {
            throw DriverUnavailable("We do not have drivers available")
        }
        val ride = req.toRide(driver)
        ride.request(driver)

        return RequestDriverResponse.fromRide(rideRepo.save(ride))
    }

    fun findById(id: Long): Driver {
        return driverRepo.findById(id).orElseThrow {
            DriverNotFoundException("Driver with id $id not found")
        }
    }

    fun getRide(id: Long): AcceptResponse {
        val ride = rideRepo.findLastRideByDriveId(id).orElseThrow {
             RideNotFoundException("You don't have any Ride")
        }
        return AcceptResponse.fromRide(ride)
    }

    fun save(driverRequest: DriverCreationRequest): Driver {
        val (latitude,longitude) =  getGeoCode(driverRequest.address)
        val driver = driverRequest.toDriver(latitude,longitude)
        driver.fillLocationFromLatLon()
        return driverRepo.save(driver)
    }

    fun delete(id: Long) {
        driverRepo.deleteById(id)
    }

    private fun getGeoCode(address:String): Pair<Double,Double>{
        val googleMapsApiGeocodeResponse = googleMapsFeignClient.getGeoCode(address,"driving", KEY)
        val results = googleMapsApiGeocodeResponse.results.firstOrNull() ?: throw RuntimeException("")
        val latitude = results.geometry.location.latitude
        val longitude = results.geometry.location.longitude
        return Pair(latitude,longitude)
    }
}