package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.item.ListenerItem
import com.briarcraft.adventure.enchant.PotionEffectEnchantments
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector

class SnowballMinigun(private val plugin: Plugin, adventureEnchants: PotionEffectEnchantments): ListenerItem {
    override val key = NamespacedKey(plugin, "snowball-minigun")
    override val type = Material.DIAMOND_HOE
    override val name = "Snowball Minigun"
    override val nameStyle = Style.style(NamedTextColor.BLUE)
    override val unbreakable = false
    override val description = listOf(
        Component.text("Santa's Merry Snowball Minigun"),
        Component.text("Left-Click to load"),
        Component.text("Right-Click to fire")
    )
    override val enchants: Map<Enchantment, Int> = mapOf(adventureEnchants.unique to 1)

    private val spread = 0.175
    private val durabilityPerShot = 5
    private val clipSize = 150

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    suspend fun on(event: PlayerInteractEvent) {
        if (event.hand == EquipmentSlot.HAND) {
            val player = event.player
            val item = player.inventory.itemInMainHand
            val meta = item.itemMeta
            if (meta is Damageable) {
                if (isInstance(meta)) {
                    when (event.action) {
                        Action.LEFT_CLICK_BLOCK,
                        Action.LEFT_CLICK_AIR -> {
                            val ammoUsed = meta.damage
                            if (ammoUsed % clipSize == 0) {
                                val sound = Sound.sound(Key.key("block.chest.locked"), Sound.Source.PLAYER, 1.5f, 0.1f)
                                plugin.server.playSound(sound, player)

                                meta.damage = meta.damage + durabilityPerShot
                                item.itemMeta = meta
                            }
                        }
                        Action.RIGHT_CLICK_BLOCK,
                        Action.RIGHT_CLICK_AIR -> {
                            event.isCancelled = true
                            val maxAmmo = item.type.maxDurability
                            val ammoUsed = meta.damage
                            val ammoRemaining = maxAmmo - ammoUsed
                            if (ammoRemaining >= 0) {
                                if (ammoUsed % clipSize == 0) {
                                    // Needs reload
                                    delay(10.ticks)
                                    val sound = Sound.sound(Key.key("entity.item.break"), Sound.Source.PLAYER, 1.5f, 0.1f)
                                    plugin.server.playSound(sound, player)
                                } else {
                                    // Fire minigun
                                    meta.damage = meta.damage + durabilityPerShot
                                    item.itemMeta = meta

                                    repeat(3) {
                                        val snowball = player.launchProjectile(Snowball::class.java)
                                        val v = snowball.velocity
                                        v.add(Vector(Math.random() * spread - spread,Math.random() * spread - spread,Math.random() * spread - spread))
                                        snowball.velocity = v.multiply(1.25)

                                        val sound = Sound.sound(Key.key("entity.snowball.throw"), Sound.Source.PLAYER, 1f, 1.3f)
                                        plugin.server.playSound(sound, player)
                                        delay(5.ticks)
                                    }
                                }
                            } else {
                                delay(10.ticks)
                                val sound = Sound.sound(Key.key("entity.item.break"), Sound.Source.PLAYER, 1.5f, 0.1f)
                                plugin.server.playSound(sound, player)

                                player.inventory.remove(item)
                            }
                        }
                        else -> {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }
}
