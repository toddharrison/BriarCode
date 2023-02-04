package com.briarcraft.adventure.api.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

class SimpleItem(
    override val key: NamespacedKey,
    override val type: Material,
    override val name: String,
    override val nameStyle: Style? = null,
    override val unbreakable: Boolean = false,
    override val description: List<Component> = listOf(),
    override val enchants: Map<Enchantment, Int> = mapOf()
): CustomItem
