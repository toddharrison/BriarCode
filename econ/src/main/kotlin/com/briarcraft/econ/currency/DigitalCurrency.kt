package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import java.math.RoundingMode
import java.text.DecimalFormat

data class DigitalCurrency(
    override val name: String,
    override val namePlural: String,
    override val symbol: String,
    override val symbolColor: NamedTextColor,
    override val format: DecimalFormat,
    override val numberColor: NamedTextColor
): Currency {
    init {
        format.roundingMode = RoundingMode.UP
    }

    override fun createMoney(amount: Double) = DigitalMoney(this, amount)

    override fun display(amount: Double, customFormat: DecimalFormat) = Component.text(symbol, symbolColor)
        .append(Component.text(customFormat.format(amount), numberColor))
}
