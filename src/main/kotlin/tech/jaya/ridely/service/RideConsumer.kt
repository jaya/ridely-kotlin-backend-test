package tech.jaya.ridely.service

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import tech.jaya.ridely.dtos.RequestDriver
import tech.jaya.ridely.model.Passenger
import tech.jaya.ridely.repository.PassengerRepo

@Service
class RideConsumer(
    private val rideService: RideService,
) {
    @RabbitListener(queues = ["nova_corrida"])
    fun consumeRideRequest(req: RequestDriver) {
        rideService.requestDriver(req)
    }
}

