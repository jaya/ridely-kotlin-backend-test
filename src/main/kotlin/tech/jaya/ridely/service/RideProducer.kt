package tech.jaya.ridely.service

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import tech.jaya.ridely.dtos.RequestDriver

@Service
class RideProducer(private val rabbitTemplate: RabbitTemplate) {
    fun sendRideRequest(req: RequestDriver) {
        rabbitTemplate.convertAndSend("corridas.exchange", "nova_corrida", req)
    }
}

