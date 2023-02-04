package com.briarcraft.econ.api.market.view

import net.kyori.adventure.text.Component
import org.bukkit.Material

class MarketViewGroup(
//    val parent: MarketViewGroup?,
    val key: String,
    val name: String,
    val icon: Material,
//    val description: List<Component>? = null,
    val items: Set<Material>? = null,
    val subGroups: List<String>? = null
//    val subGroups: List<MarketViewGroup> = listOf()
) {
    init {
        require(items.isNullOrEmpty() xor subGroups.isNullOrEmpty())
    }

    fun getAllItems(groups: Map<String, MarketViewGroup>): Set<Material> =
        (items ?: setOf()) + (subGroups?.mapNotNull { groups[it] }?.flatMap { it.getAllItems(groups) } ?: setOf())

    override fun toString() = "MarketViewGroup($key)"
}