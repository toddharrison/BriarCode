package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.Money
import java.math.BigDecimal
import java.math.RoundingMode

class DigitalMoney(
    override val currency: Currency,
    amount: Double
): Money {
    override val amount = BigDecimal(amount)
        .setScale(currency.precision(), RoundingMode.UP)
        .toDouble()

    override fun display() = currency.display(amount)
}
