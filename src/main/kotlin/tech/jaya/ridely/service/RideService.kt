package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.ActionRideRequest
import tech.jaya.ridely.dtos.CancelResponse
import tech.jaya.ridely.dtos.FinishResponse
import tech.jaya.ridely.dtos.FinishRideRequest
import tech.jaya.ridely.dtos.RefuseResponse
import tech.jaya.ridely.dtos.RequestDriver
import tech.jaya.ridely.dtos.RequestDriverResponse
import tech.jaya.ridely.exception.DriverUnavailable
import tech.jaya.ridely.exception.RideInvalidState
import tech.jaya.ridely.exception.RideNotFoundException
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val googleMapsService: GoogleMapsService,
    private val googleGeocodingService: GoogleGeocodingService,
    private val driverService: DriverService,
    private val passengerService: PassengerService
) {
    /**
     * Solicita o motorista disponível mais próximo para uma nova corrida.
     * Obtém informações de rota e localização, tenta encontrar o motorista mais próximo
     * filtrando por cidade e sublocalidade (se disponíveis), ou por proximidade geográfica.
     * Calcula preço, distância e duração, cria e salva a corrida.
     *
     * @param req dados da solicitação de corrida (origem e destino)
     * @throws RideInvalidState se não for possível obter informações da rota
     * @throws DriverUnavailable se não houver motoristas disponíveis próximos
     */
    fun requestDriver(req: RequestDriver) {

        val passenger = passengerService.getPassengerById(req.passengerId)

        val routeInfo = googleMapsService.getRouteInfo(
            req.pickUp,
            req.dropOff
        )

        if (routeInfo.duration == 0|| routeInfo.distance == 0 || routeInfo.startLng == 0.0 || routeInfo.startLat == 0.0) {
            throw RideInvalidState("Unable to get route information for the race")
        }

        val (distance, duration, startLat, startLng) = routeInfo
        val locationInfo = googleGeocodingService.getLocationInfo(startLat, startLng)

        val driver = if (locationInfo != null && locationInfo.city != null && locationInfo.sublocality != null) {
            driverService.findNearestDriverByLocationInfo(
                locationInfo.city,
                locationInfo.sublocality,
                startLat,
                startLng
            ) ?: driverService.findNearestDriver(startLat, startLng)
        } else {
            driverService.findNearestDriver(startLat, startLng)
        }

        if (driver == null) {
            throw DriverUnavailable("No drivers found near the reported location")
        }

        val durationInMinutes = secondsToMinutes(duration)
        val distanceInKilometers = metersToKilometers(distance)
        val price = calculateRidePrice(distanceInKilometers, durationInMinutes)
        val ride = req.toRide(driver, passenger)
        ride.request(driver, passenger, distanceInKilometers, durationInMinutes, price)
        rideRepo.save(ride)
        driverService.saveDriver(driver)
        passengerService.save(passenger)

    }

    /**
     * Converte uma distância em metros para quilômetros, arredondando para cima.
     *
     * @param meters Distância em metros.
     * @return Distância em quilômetros (arredondada para cima).
     */
    fun metersToKilometers(meters: Int): Int {
        return ceil(meters / 1000.0).toInt()
    }

    /**
     * Converte uma duração em segundos para minutos, arredondando para cima.
     *
     * @param seconds Duração em segundos.
     * @return Duração em minutos (arredondada para cima).
     */
    fun secondsToMinutes(seconds: Int): Int {
        return ceil(seconds / 60.0).toInt()
    }

    /**
     * Calcula o preço final da corrida com base na distância (km), duração (min) e taxa do app.
     *
     * @param distanceKm Distância em quilômetros.
     * @param durationMin Duração em minutos.
     * @return Valor final da corrida como BigDecimal, com duas casas decimais.
     */
    fun calculateRidePrice(distanceKm: Int, durationMin: Int): BigDecimal {
        val basePrice = (distanceKm * 3.0) + (durationMin * 2.0)
        val appFee = basePrice * 0.01
        val total = basePrice + appFee
        return BigDecimal(total).setScale(2, RoundingMode.HALF_UP)
    }

    /**
     * Busca a última corrida solicitada por um passageiro específico.
     *
     * @param passengerId Identificador do passageiro.
     * @return RequestDriverResponse com os dados da última corrida solicitada pelo passageiro,
     * ou null se não houver corridas.
     */
    fun findLastRideByPassengerId(passengerId: Long): RequestDriverResponse? {
        return rideRepo.findLastRideByPassengerId(passengerId)
            .orElse(null)
            ?.let { RequestDriverResponse.fromRide(it) }
    }


    /**
     * Recusa uma corrida existente.
     *
     * @param req Dados da requisição para recusar a corrida.
     * @return RefuseResponse com os dados da corrida recusada.
     * @throws RideNotFoundException se a corrida não for encontrada.
     */
    fun refuseRide(req: ActionRideRequest): RefuseResponse {
        val ride = rideRepo.findById(req.id).orElseThrow { RideNotFoundException("No ride found with id ${req.id}") }
        ride.refuse()
        return RefuseResponse.fromRide(rideRepo.save(ride))
    }

    /**
     * Cancela uma corrida existente.
     *
     * @param req Dados da requisição para cancelar a corrida.
     * @return CancelResponse com os dados da corrida cancelada.
     * @throws RideNotFoundException se a corrida não for encontrada.
     */
    fun cancelRide(req: ActionRideRequest): CancelResponse {
        val ride = rideRepo.findById(req.id).orElseThrow { RideNotFoundException("No ride found with id ${req.id}") }
        ride.cancel()
        return CancelResponse.fromRide(rideRepo.save(ride))
    }

    /**
     * Finaliza uma corrida existente, informando o valor.
     *
     * @param req Dados da requisição para finalizar a corrida.
     * @return FinishResponse com os dados da corrida finalizada.
     * @throws RideNotFoundException se a corrida não for encontrada.
     */
    fun finishRide(req: FinishRideRequest): FinishResponse {
        val (id, price) = req
        val ride = rideRepo.findById(id).orElseThrow { RideNotFoundException("No ride found with id $id") }
        ride.complete(price)
        return FinishResponse.fromRide(rideRepo.save(ride))
    }

    /**
     * Aceita uma corrida existente.
     *
     * @param req Dados da requisição para aceitar a corrida.
     * @return AcceptResponse com os dados da corrida aceita.
     * @throws RideNotFoundException se a corrida não for encontrada.
     */
    fun acceptRide(req: ActionRideRequest): AcceptResponse {
        val ride = rideRepo.findById(req.id).orElseThrow { RideNotFoundException("No ride found with id ${req.id}") }
        ride.accept()
        return AcceptResponse.fromRide(rideRepo.save(ride))
    }

    /**
     * Remove uma corrida pelo id.
     *
     * @param id Identificador da corrida a ser removida.
     */
    fun delete(id: Long) {
        rideRepo.deleteById(id)
    }
}