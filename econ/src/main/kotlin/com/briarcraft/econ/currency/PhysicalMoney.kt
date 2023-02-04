package com.briarcraft.econ.currency

import com.briarcraft.econ.api.currency.Money
import org.bukkit.Material

class PhysicalMoney(
    override val currency: PhysicalCurrency,
    val quantity: Int,
    val type: Material
): Money {
    override val amount = quantity.toDouble()

    override fun display() = currency.display(amount)
}
