package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.item.CraftableItem
import com.briarcraft.adventure.api.item.CustomItem
import com.briarcraft.adventure.api.item.ListenerItem
import com.briarcraft.adventure.api.item.SimpleItem
import com.briarcraft.adventure.enchant.PotionEffectEnchantments
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.java.JavaPlugin

class CustomItems(plugin: JavaPlugin, enchants: PotionEffectEnchantments) {
    val adventureRod = AdventureRod(plugin, enchants)

    val rockHammer = RockHammer(plugin)
    val filterHopper = FilterHopper(plugin)
    val infiniteDispenser = InfiniteDispenser(plugin)
    val infiniteDropper = InfiniteDropper(plugin)

    val snowballMinigun = SnowballMinigun(plugin, enchants)
    val bunnyBoots = SimpleItem(NamespacedKey(plugin, "bunny-boots"), Material.LEATHER_BOOTS, "Bunny Boots",
        enchants = mapOf(enchants.unique to 1, enchants.jump to 10))
    val santaBoots = SimpleItem(NamespacedKey(plugin, "santa-boots"), Material.LEATHER_BOOTS, "Santa's Boots",
        enchants = mapOf(enchants.unique to 1, enchants.jump to 3, enchants.slowFalling to 1))
    val santaClause = SimpleItem(NamespacedKey(plugin, "santa-clause"), Material.LEATHER_CHESTPLATE, "The Santa Clause",
        description = listOf(Component.text("Be sure to read the fine print...")),
        unbreakable = true,
        enchants = mapOf(enchants.unique to 1, Enchantment.BINDING_CURSE to 1, Enchantment.PROTECTION_ENVIRONMENTAL to 5, Enchantment.PROTECTION_FIRE to 10))
    val santaSlayer = SimpleItem(NamespacedKey(plugin, "santa-slayer"), Material.GOLDEN_SWORD, "Santa's Slayer",
        enchants = mapOf(enchants.unique to 1, Enchantment.DAMAGE_ALL to 10, Enchantment.DAMAGE_UNDEAD to 10, Enchantment.DAMAGE_ARTHROPODS to 10, Enchantment.SWEEPING_EDGE to 5, Enchantment.KNOCKBACK to 5))
    val icePick = SimpleItem(NamespacedKey(plugin, "ice-pick"), Material.DIAMOND_PICKAXE, "Ice Pick",
        enchants = mapOf(enchants.unrepairable to 1, Enchantment.DAMAGE_ALL to 5, Enchantment.DIG_SPEED to 3, Enchantment.SILK_TOUCH to 1))
    val hoeHoeHoe = SimpleItem(NamespacedKey(plugin, "hoe-hoe-hoe"), Material.IRON_HOE, "Hoe, Hoe, Hoe!",
        enchants = mapOf(enchants.unrepairable to 1, Enchantment.DIG_SPEED to 10))

    val items: Set<CustomItem> = setOf(
        adventureRod,
        rockHammer, filterHopper, infiniteDispenser, infiniteDropper,
        bunnyBoots,
        santaBoots, santaClause, santaSlayer, icePick, hoeHoeHoe,
        snowballMinigun,
    )

    init {
        items.forEach { item ->
            if (item is CraftableItem) Bukkit.addRecipe(item.recipe)
            if (item is ListenerItem) plugin.server.pluginManager.registerSuspendingEvents(item, plugin)
        }
    }
}
