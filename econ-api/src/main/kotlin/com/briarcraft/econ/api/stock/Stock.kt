package com.briarcraft.econ.api.stock

data class Stock(
    val removable: Double? = null,
    val addable: Double? = null
) {
    fun getMaxAmount(): Double? {
        return if (removable != null && addable != null) removable + addable
        else null
    }
}