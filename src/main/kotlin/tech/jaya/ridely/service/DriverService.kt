package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.exception.DriverNotFound
import tech.jaya.ridely.exception.RideNotFoundException
import tech.jaya.ridely.dtos.DriverResponse
import tech.jaya.ridely.dtos.DriverCreation
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.DriverLocationUpdate
import tech.jaya.ridely.dtos.toResponse
import tech.jaya.ridely.model.Driver
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Service
class DriverService(
    private val driverRepo: DriverRepo,
    private val rideRepo: RideRepo,
    private val geocodingService: GoogleGeocodingService
) {

    /**
     * Busca um motorista pelo id.
     *
     * @param id Identificador do motorista.
     * @return DriverResponse com os dados do motorista encontrado.
     * @throws DriverNotFound se o motorista não for encontrado.
     */
    fun findById(id: Long): DriverResponse {
        val driver = driverRepo.findById(id).orElseThrow {
            DriverNotFound("Drive not found $id")
        }
        return driver.toResponse()
    }

    /**
     * Encontra o motorista disponível mais próximo de uma localização de origem.
     *
     * @param originLat Latitude da localização de origem.
     * @param originLon Longitude da localização de origem.
     * @return O motorista mais próximo ou null se não houver motoristas disponíveis.
     */
    fun findNearestDriver(originLat: Double, originLon: Double): Driver? {
        val availableDrivers = driverRepo.findAll().filter { it.available }
        return availableDrivers.minByOrNull { haversine(originLat, originLon, it.latitude, it.longitude) }
    }

    /**
    * Calcula a distância entre dois pontos geográficos usando a fórmula de Haversine.
    *
    * @param lat1 Latitude do primeiro ponto.
    * @param lon1 Longitude do primeiro ponto.
    * @param lat2 Latitude do segundo ponto.
    * @param lon2 Longitude do segundo ponto.
    * @return Distância em quilômetros entre os dois pontos.
    */
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Raio da Terra em km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * Encontra o motorista mais próximo em uma cidade e sublocalidade específicas.
     *
     * @param city Cidade onde buscar motoristas.
     * @param sublocality Sublocalidade onde buscar motoristas.
     * @param lat Latitude de referência.
     * @param lon Longitude de referência.
     * @return O motorista mais próximo ou null se não houver motoristas na região.
     */
    fun findNearestDriverByLocationInfo(
        city: String,
        sublocality: String,
        lat: Double,
        lon: Double
    ): Driver? {
        val drivers = driverRepo.findByCityAndSublocality(city, sublocality)
        return drivers.minByOrNull { haversine(lat, lon, it.latitude, it.longitude) }
    }

    /**
     * Atualiza a localização de um motorista e salva as informações de cidade e sublocalidade.
     *
     * @param id Identificador do motorista.
     * @param location Dados de latitude e longitude para atualização.
     * @return DriverResponse com os dados atualizados do motorista.
     * @throws DriverNotFound se o motorista não for encontrado.
     */
    fun updateLocation(id: Long, location: DriverLocationUpdate): DriverResponse {
        val driver = driverRepo.findById(id).orElseThrow { DriverNotFound("Driver not found $id") }
        driver.latitude = location.latitude
        driver.longitude = location.longitude

        val locationInfo = geocodingService.getLocationInfo(location.latitude, location.longitude)
        if (locationInfo != null) {
            driver.city = locationInfo.city ?: ""
            driver.sublocality = locationInfo.sublocality ?: ""
        }

        driverRepo.save(driver)
        return driver.toResponse()
    }

    /**
     * Busca a última corrida associada ao motorista informado.
     *
     * @param id Identificador do motorista.
     * @return AcceptResponse com os dados da última corrida.
     * @throws RideNotFoundException se o motorista não possuir corridas.
     */
    fun getLastRide(id: Long): AcceptResponse {
        val ride = rideRepo.findLastRideByDriveId(id).orElseThrow {
            throw RideNotFoundException("You don't have any Ride")
        }
        return AcceptResponse.fromRide(ride)
    }

    /**
     * Salva um novo motorista no sistema.
     *
     * @param driverRequest Dados para criação do motorista.
     * @return DriverResponse com os dados do motorista salvo.
     */
    fun save(driverRequest: DriverCreation): DriverResponse {
        val driver = driverRepo.save(driverRequest.toDriver())
        return driver.toResponse()
    }

    /**
     * Salva as mudanças do motorista no sistema.
     *
     * @param driver Dados do motorista.
     * @return DriverResponse com os dados do motorista salvo.
     */
    fun saveDriver(driver: Driver): Driver {
        return driverRepo.save(driver)
    }

    /**
     * Remove um motorista pelo id.
     *
     * @param id Identificador do motorista a ser removido.
     */
    fun delete(id: Long) {
        driverRepo.deleteById(id)
    }
}