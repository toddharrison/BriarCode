package com.briarcraft.econ.api.currency

import net.kyori.adventure.text.TextComponent

interface Money {
    val currency: Currency
    val amount: Double

    fun display(): TextComponent

    operator fun minus(money: Money) = require(currency == money.currency)
        .let { currency.createMoney(amount - money.amount) }
    operator fun minus(amount: Double) = currency.createMoney(this.amount - amount)
    operator fun plus(money: Money) = require(currency == money.currency)
        .let { currency.createMoney(amount + money.amount) }
    operator fun plus(amount: Double) = currency.createMoney(this.amount + amount)
    operator fun times(multiplier: Double) = currency.createMoney(amount * multiplier)
    operator fun div(divisor: Double) = currency.createMoney(amount / divisor)
}
