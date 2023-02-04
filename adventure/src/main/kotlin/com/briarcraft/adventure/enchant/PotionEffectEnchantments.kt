package com.briarcraft.adventure.enchant

import com.briarcraft.adventure.api.enchant.listener.ListenerEnchantment
import com.briarcraft.adventure.api.enchant.potion.PotionBlessEffectEnchantment
import com.briarcraft.adventure.api.enchant.potion.PotionCurseEffectEnchantment
import com.briarcraft.adventure.api.enchant.usage.*
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType

class PotionEffectEnchantments(plugin: JavaPlugin) {
    val absorption = PotionBlessEffectEnchantment(NamespacedKey(plugin, "absorption"), 256 / 4, "Absorption", armorEnchantmentUsage, PotionEffectType.ABSORPTION)
    val conduit = PotionBlessEffectEnchantment(NamespacedKey(plugin, "conduit"), 256, "Conduit", chestplateEnchantmentUsage, PotionEffectType.CONDUIT_POWER)
    val damageResistance = PotionBlessEffectEnchantment(NamespacedKey(plugin, "damage-resistance"), 256 / 4, "Damage Resistance", armorEnchantmentUsage, PotionEffectType.DAMAGE_RESISTANCE)
    val dolphinsGrace = PotionBlessEffectEnchantment(NamespacedKey(plugin, "dolphins-grace"), 256, "Dolphin's Grace", bootsEnchantmentUsage, PotionEffectType.DOLPHINS_GRACE)
    val fastDigging = PotionBlessEffectEnchantment(NamespacedKey(plugin, "fast-digging"), 256, "Fast Digging", chestplateEnchantmentUsage, PotionEffectType.FAST_DIGGING)
    val fireResistance = PotionBlessEffectEnchantment(NamespacedKey(plugin, "fire-resistance"), 256 / 4, "Fire Resistance", armorEnchantmentUsage, PotionEffectType.FIRE_RESISTANCE)
    val healthBoost = PotionBlessEffectEnchantment(NamespacedKey(plugin, "health-boost"), 256, "Health Boost", chestplateEnchantmentUsage, PotionEffectType.HEALTH_BOOST)
    val heroOfTheVillage = PotionBlessEffectEnchantment(NamespacedKey(plugin, "hero-of-the-village"), 256, "Hero of the Village", helmetEnchantmentUsage, PotionEffectType.HERO_OF_THE_VILLAGE)
    val increaseDamage = PotionBlessEffectEnchantment(NamespacedKey(plugin, "increase-damage"), 256, "Increase Damage", chestplateEnchantmentUsage, PotionEffectType.INCREASE_DAMAGE)
    val invisibility = PotionBlessEffectEnchantment(NamespacedKey(plugin, "invisibility"), 256, "Invisibility", chestplateEnchantmentUsage, PotionEffectType.INVISIBILITY)
    val jump = PotionBlessEffectEnchantment(NamespacedKey(plugin, "jump"), 256, "Jump", bootsEnchantmentUsage, PotionEffectType.JUMP)
    val luck = PotionBlessEffectEnchantment(NamespacedKey(plugin, "luck"), 256, "Luck", helmetEnchantmentUsage, PotionEffectType.LUCK)
    val nightVision = PotionBlessEffectEnchantment(NamespacedKey(plugin, "night-vision"), 256, "Night Vision", helmetEnchantmentUsage, PotionEffectType.NIGHT_VISION)
    val regeneration = PotionBlessEffectEnchantment(NamespacedKey(plugin, "regeneration"), 256, "Regeneration", chestplateEnchantmentUsage, PotionEffectType.REGENERATION)
    val saturation = PotionBlessEffectEnchantment(NamespacedKey(plugin, "saturation"), 256, "Saturation", helmetEnchantmentUsage, PotionEffectType.SATURATION)
    val slowFalling = PotionBlessEffectEnchantment(NamespacedKey(plugin, "slow-falling"), 256, "Slow Falling", bootsEnchantmentUsage, PotionEffectType.SLOW_FALLING)
    val speed = PotionBlessEffectEnchantment(NamespacedKey(plugin, "speed"), 256, "Speed", bootsEnchantmentUsage, PotionEffectType.SPEED)
    val waterBreathing = PotionBlessEffectEnchantment(NamespacedKey(plugin, "water-breathing"), 256, "Water Breathing", helmetEnchantmentUsage, PotionEffectType.WATER_BREATHING)

    val badOmen = PotionCurseEffectEnchantment(NamespacedKey(plugin, "bad-omen"), 256, "Bad Omen", armorEnchantmentUsage, PotionEffectType.BAD_OMEN)
    val blindness = PotionCurseEffectEnchantment(NamespacedKey(plugin, "blindness"), 256, "Blindness", armorEnchantmentUsage, PotionEffectType.BLINDNESS)
    val confusion = PotionCurseEffectEnchantment(NamespacedKey(plugin, "confusion"), 256, "Confusion", armorEnchantmentUsage, PotionEffectType.CONFUSION)
    val darkness = PotionCurseEffectEnchantment(NamespacedKey(plugin, "darkness"), 256, "Darkness", armorEnchantmentUsage, PotionEffectType.DARKNESS)
    val glowing = PotionCurseEffectEnchantment(NamespacedKey(plugin, "glowing"), 256, "Glowing", armorEnchantmentUsage, PotionEffectType.GLOWING)
    val hunger = PotionCurseEffectEnchantment(NamespacedKey(plugin, "hunger"), 256, "Hunger", armorEnchantmentUsage, PotionEffectType.HUNGER)
    val levitation = PotionCurseEffectEnchantment(NamespacedKey(plugin, "levitation"), 256, "Levitation", armorEnchantmentUsage, PotionEffectType.LEVITATION)
    val poison = PotionCurseEffectEnchantment(NamespacedKey(plugin, "poison"), 256, "Poison", armorEnchantmentUsage, PotionEffectType.POISON)
    val slow = PotionCurseEffectEnchantment(NamespacedKey(plugin, "slow"), 256, "Slow", armorEnchantmentUsage, PotionEffectType.SLOW)
    val slowDigging = PotionCurseEffectEnchantment(NamespacedKey(plugin, "slow-digging"), 256, "Slow Digging", armorEnchantmentUsage, PotionEffectType.SLOW_DIGGING)
    val weakness = PotionCurseEffectEnchantment(NamespacedKey(plugin, "weakness"), 256, "Weakness", armorEnchantmentUsage, PotionEffectType.WEAKNESS)
    val unluck = PotionCurseEffectEnchantment(NamespacedKey(plugin, "unluck"), 256, "Unluck", armorEnchantmentUsage, PotionEffectType.UNLUCK)
    val wither = PotionCurseEffectEnchantment(NamespacedKey(plugin, "wither"), 256, "Wither", armorEnchantmentUsage, PotionEffectType.WITHER)

    // Other
//    val HEAL: PotionEffectType = PotionEffectTypeWrapper(6, "instant_health")
//    val HARM: PotionEffectType = PotionEffectTypeWrapper(7, "instant_damage")

    val unique = UniqueEnchantment(plugin)
    val unrepairable = UnrepairableEnchantment(plugin)

    val enchantments = setOf(
        absorption, conduit, damageResistance, dolphinsGrace, fastDigging, fireResistance, healthBoost, heroOfTheVillage, increaseDamage, invisibility, jump, luck, nightVision, regeneration, saturation, slowFalling, speed, waterBreathing,
        badOmen, blindness, confusion, darkness, glowing, hunger, levitation, poison, slow, slowDigging, weakness, unluck, wither,
        unique,
        unrepairable
    )

    init {
        enchantments.forEach { enchant ->
            if (enchant is ListenerEnchantment) plugin.server.pluginManager.registerSuspendingEvents(enchant, plugin)
        }
    }
}
