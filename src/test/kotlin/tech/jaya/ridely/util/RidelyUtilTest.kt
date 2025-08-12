package tech.jaya.ridely.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RidelyUtilTest {

    private val util = RidelyUtil()

    @Test
    fun shouldCalculatePriceFor34KmAnd12MinReturning33_86() {
        val result = util.calculaPrice(3.4, 12.0)
        assertEquals(BigDecimal("33.86"), result)
    }

    @Test
    fun shouldApplyHalfEvenCorrectlyInTheRateRounding() {
        val result = util.calculaPrice(0.5, 0.0)
        assertEquals(BigDecimal("1.48"), result)
    }

    @Test
    fun shouldReturn0_00ForDistanceAndTimeZero() {
        val result = util.calculaPrice(0.0, 0.0)
        assertEquals(BigDecimal("0.00"), result)
    }

    @Test
    fun shouldKeepTwoDecimalPlacesInTheResult() {
        val result = util.calculaPrice(1.2345, 6.789)
        assertEquals(2, result.scale())
    }
}
