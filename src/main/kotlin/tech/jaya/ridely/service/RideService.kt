package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.repository.DriverRepo
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
import tech.jaya.ridely.exception.RideNotFoundException
import tech.jaya.ridely.model.Passenger

@Service
class RideService(
    private val rideRepo: RideRepo,
    private val driverRepo: DriverRepo
) {
    /**
     * Solicita um motorista disponível para uma nova corrida.
     *
     * @param req Dados da solicitação de corrida.
     * @return RequestDriverResponse com os dados da corrida criada.
     * @throws DriverUnavailable se não houver motoristas disponíveis.
     */
    fun requestDriver(req: RequestDriver, passenger: Passenger): RequestDriverResponse {
        val driver = driverRepo.findAvailableDriver().orElseThrow {
            DriverUnavailable("We do not have drivers available")
        }
        val ride = req.toRide(driver, passenger)
        ride.request(driver, passenger)
        return RequestDriverResponse.fromRide(rideRepo.save(ride))
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