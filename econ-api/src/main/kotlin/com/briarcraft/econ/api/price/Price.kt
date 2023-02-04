package com.briarcraft.econ.api.price

data class Price(
    val maxPrice: Double,
    val minPrice: Double? = null,
    val curve: Double? = null
)
