package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Currency
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import java.math.RoundingMode
import java.text.DecimalFormat

class PhysicalCurrency(
    override val name: String,
    override val namePlural: String,
    override val symbol: String,
    override val symbolColor: NamedTextColor,
    override val format: DecimalFormat,
    override val numberColor: NamedTextColor,
    val type: Material
): Currency {
    init {
        format.roundingMode = RoundingMode.UP
    }

    override fun createMoney(amount: Double): PhysicalMoney {
        require(amount.rem(1).equals(0.0))
        return createMoney(amount.toInt())
    }
    fun createMoney(amount: Int) = PhysicalMoney(this, amount, type)

    override fun display(amount: Double, customFormat: DecimalFormat) = Component.text(symbol, symbolColor)
        .append(Component.text(customFormat.format(amount), numberColor))
}
