package com.briarcraft.adventure.event

import com.briarcraft.adventure.api.enchant.potion.PotionEffectEnchantment
import com.briarcraft.kotlin.util.isSimilarIgnoringDamage
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PotionEffectEnchantListener: Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PlayerArmorChangeEvent) {
        val player = event.player
        val oldItem = event.oldItem
        val newItem = event.newItem

        if (oldItem == null || !oldItem.isSimilarIgnoringDamage(newItem)) {
            if (oldItem != null) {
                // Remove effects from old item
                val oldEffectTypes = oldItem.enchantments
                    .mapNotNull { (enchant, level) -> if (enchant is PotionEffectEnchantment) enchant.effectType to level else null }
                oldEffectTypes.forEach { (type, level) ->
                    val playerEffectLevel = player.activePotionEffects
                        .filter { it.isInfinite }
                        .filter { it.type == type }
                        .sumOf { it.amplifier + 1 }
                    val newLevel = playerEffectLevel - level
                    player.removePotionEffect(type)
                    if (newLevel > 0) {
                        updateEffect(player, type, newLevel)
                    }
                }
            }
            if (newItem != null) {
                // Add effects from new item
                val newEffectTypes = newItem.enchantments
                    .mapNotNull { (enchant, level) -> if (enchant is PotionEffectEnchantment) enchant.effectType to level else null }
                newEffectTypes.forEach { (type, level) ->
                    val playerEffectLevel = player.activePotionEffects
                        .filter { it.isInfinite }
                        .filter { it.type == type }
                        .sumOf { it.amplifier + 1 }
                    val newLevel = playerEffectLevel + level
                    player.removePotionEffect(type)
                    if (newLevel > 0) {
                        updateEffect(player, type, newLevel)
                    }
                }
            }
        }
    }

    private fun updateEffect(player: Player, effectType: PotionEffectType, level: Int) {
        val duration = -1
        val amplifier = level - 1
        val ambient = false
        val particles = false
        val effect = PotionEffect(effectType, duration, amplifier, ambient, particles)
        player.addPotionEffect(effect)
    }
}
