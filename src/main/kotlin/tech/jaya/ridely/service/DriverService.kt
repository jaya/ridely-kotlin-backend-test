package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.repository.DriverRepo
import tech.jaya.ridely.repository.RideRepo
import tech.jaya.ridely.exception.DriverNotFound
import tech.jaya.ridely.exception.RideNotFoundException
import tech.jaya.ridely.dtos.DriverResponse
import tech.jaya.ridely.dtos.DriverCreation
import tech.jaya.ridely.dtos.AcceptResponse
import tech.jaya.ridely.dtos.toResponse

@Service
class DriverService(
    private val driverRepo: DriverRepo,
    private val rideRepo: RideRepo
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
     * Remove um motorista pelo id.
     *
     * @param id Identificador do motorista a ser removido.
     */
    fun delete(id: Long) {
        driverRepo.deleteById(id)
    }
}