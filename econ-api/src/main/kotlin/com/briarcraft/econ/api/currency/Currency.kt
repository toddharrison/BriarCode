package com.briarcraft.econ.api.currency

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import java.math.RoundingMode
import java.text.DecimalFormat

interface Currency {
    val name: String
    val namePlural: String
    val symbol: String
    val symbolColor: NamedTextColor
    val format: DecimalFormat
    val numberColor: NamedTextColor

    fun createMoney(amount: Double): Money
    fun display(amount: Double, customFormat: DecimalFormat = format): TextComponent

    fun precision() = format.maximumFractionDigits
}
