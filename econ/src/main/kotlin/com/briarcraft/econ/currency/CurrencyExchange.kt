package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.Exchange
import com.briarcraft.econ.api.currency.Money

class CurrencyExchange(
    override val rates: Map<Currency, Map<Currency, Double>>
): Exchange {
    override fun getRate(fromCurrency: Currency, toCurrency: Currency): Double? = rates[fromCurrency]?.get(toCurrency)

    override fun exchange(money: Money, toCurrency: Currency) = getRate(money.currency, toCurrency)
        ?.let { rate ->
            toCurrency.createMoney(money.amount * rate)
        }
}
