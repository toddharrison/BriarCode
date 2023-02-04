package com.briarcraft.econ.api.currency

interface Exchange {
    val rates: Map<Currency, Map<Currency, Double>>

    fun getRate(fromCurrency: Currency, toCurrency: Currency): Double?
    fun exchange(money: Money, toCurrency: Currency): Money?
}
