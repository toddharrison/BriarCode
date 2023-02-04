package com.briarcraft.adventure.event

import com.briarcraft.adventure.api.enchant.potion.PotionEffectEnchantment
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PotionEffectEnchantListener: Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: PlayerArmorChangeEvent) {
        val player = event.player
        player.activePotionEffects.forEach { effect -> player.removePotionEffect(effect.type) }
        player.inventory.armorContents
            .filterNotNull()
            .flatMap { it.enchantments.entries }
            .mapNotNull { (enchant, level) -> if (enchant is PotionEffectEnchantment) enchant.effectType to level else null }
            .groupBy { (effect, _) -> effect }
            .map { (effect, levels) -> effect to levels.sumOf { it.second } }
            .forEach { (effect, level) -> updateEffect(player, effect, level) }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: EntityPotionEffectEvent) {
        val entity = event.entity
        if (entity is Player && event.cause == EntityPotionEffectEvent.Cause.EXPIRATION) {
            val effectType = event.modifiedType
            val amplifier = entity.inventory.armorContents
                .filterNotNull()
                .flatMap { it.enchantments.entries }
                .filter { (enchant, _) -> enchant is PotionEffectEnchantment && enchant.effectType == effectType }
                .sumOf { (_, level) -> level }
            if (amplifier > 0) {
                updateEffect(entity, effectType, amplifier)
            }
        }
    }

    private fun updateEffect(player: Player, effectType: PotionEffectType, level: Int) {
        val duration = 20 * 100_000
        val amplifier = level - 1
        val ambient = false
        val particles = false
        val effect = PotionEffect(effectType, duration, amplifier, ambient, particles)
        player.addPotionEffect(effect)
    }
}
