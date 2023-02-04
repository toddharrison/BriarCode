package com.briarcraft.econ.api.item

import org.bukkit.Material

data class ItemAmount(
    val type: Material,
    val amount: Double
) {
    init { require(amount >= 0) }
}
