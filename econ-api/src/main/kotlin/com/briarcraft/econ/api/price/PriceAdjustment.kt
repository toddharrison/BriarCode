package com.briarcraft.econ.api.price

class PriceAdjustment(
    priceMultiplier: Double? = null,
    feePercentage: Double? = null,
    feeMinimum: Double? = null,
    val modifier: (Double, Double) -> Double = { value, adjustment -> value + adjustment }
) {
    val priceMultiplier: Double = priceMultiplier ?: 1.0
    val feePercentage: Double = feePercentage ?: 0.0
    val feeMinimum: Double = feeMinimum ?: 0.0
}
