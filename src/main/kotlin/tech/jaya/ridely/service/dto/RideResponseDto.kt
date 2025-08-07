package tech.jaya.ridely.service

data class RideResponseDto(
    val tempoEstimadoMinutos: Double,
    val distanciaKm: Double,
    val precoEstimado: BigDecimal,
    val taxaApp: BigDecimal
)