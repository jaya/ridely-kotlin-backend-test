package tech.jaya.ridely.util

import java.math.BigDecimal
import java.math.RoundingMode

class RideUtil {

    fun calculaPrice(kmDistance: Double, timeMin: Double): BigDecimal {
        val price = (kmDistance * 3.0 + timeMin * 2.0).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        val taxaApp = price.multiply(BigDecimal("0.01")).setScale(2, RoundingMode.HALF_EVEN)

        return price.subtract(taxaApp).setScale(2, RoundingMode.HALF_EVEN)
    }
}