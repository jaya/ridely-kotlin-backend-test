package tech.jaya.ridely.service

import org.springframework.stereotype.Service
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.repository.PassengerRepo
import tech.jaya.ridely.exception.PassengerNotFoundException

@Service
class PassengerService(
    private val passengerRepository: PassengerRepo
) {

    /**
     * Salva um novo passageiro no banco de dados.
     * @param passenger Passageiro a ser salvo.
     * @return Passageiro salvo.
     */
    fun save(passenger: Passenger): Passenger {
        return passengerRepository.save(passenger)
    }

    /**
     * Remove um passageiro pelo id.
     * @param id Identificador do passageiro.
     */
    fun delete(id: Long) {
        passengerRepository.deleteById(id)
    }

    /**
     * Atualiza os dados de um passageiro existente.
     * @param id Identificador do passageiro a ser atualizado.
     * @param updated Dados atualizados do passageiro.
     * @return Passageiro atualizado.
     * @throws PassengerNotFoundException se o passageiro não for encontrado.
     */
    fun updatePassenger(id: Long, updated: Passenger): Passenger {
        val passenger = passengerRepository.findById(id)
            .orElseThrow {( PassengerNotFoundException("Passenger not found with id $id") )}
        passenger.name = updated.name
        passenger.email = updated.email
        passenger.inTraveling = updated.inTraveling
        passenger.activationDate = updated.activationDate
        return passengerRepository.save(passenger)
    }

    /**
     * Busca um passageiro pelo id.
     * @param id Identificador do passageiro.
     * @return Passageiro encontrado.
     * @throws PassengerNotFoundException se o passageiro não for encontrado.
     */
    fun getPassengerById(id: Long): Passenger {
        return passengerRepository.findById(id)
            .orElseThrow { PassengerNotFoundException("Passenger not found with id $id") }
    }
}