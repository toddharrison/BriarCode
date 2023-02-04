package com.briarcraft.adventure.api.enchant.listener

import com.briarcraft.adventure.api.enchant.NameEnchantment
import org.bukkit.NamespacedKey

open class ListenerNameEnchantment(
    key: NamespacedKey,
    enchantName: String
): ListenerEnchantment, NameEnchantment(key, enchantName)
