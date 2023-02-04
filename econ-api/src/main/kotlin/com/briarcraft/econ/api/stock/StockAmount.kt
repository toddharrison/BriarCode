package com.briarcraft.econ.api.stock

data class StockAmount(var curAmount: Double, val maxAmount: Double? = null) {
    init {
        require(curAmount >= 0)
        if (maxAmount != null) require(curAmount <= maxAmount)
    }
}