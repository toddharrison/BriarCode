package com.briarcraft.econ.api.recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class CachedItemStack(
    cachedType: Material,
    cachedAmount: Int = 1
): ItemStack(cachedType, cachedAmount) {
    constructor(itemStack: ItemStack) : this(itemStack.type, itemStack.amount)

    override fun toString() = "CachedItemStack(material=$type, amount=$amount)"
}
