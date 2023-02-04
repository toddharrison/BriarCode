package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.item.ListenerItem
import com.briarcraft.adventure.enchant.PotionEffectEnchantments
import com.destroystokyo.paper.loottable.LootableEntityInventory
import com.destroystokyo.paper.loottable.LootableInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Barrel
import org.bukkit.block.Chest
import org.bukkit.block.ShulkerBox
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.StorageMinecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.InventoryHolder
import org.bukkit.loot.Lootable
import org.bukkit.plugin.Plugin

class AdventureRod(plugin: Plugin, adventureEnchants: PotionEffectEnchantments): ListenerItem {
    private val adventureRodPermission = "briar.adventure.rod"

    override val key = NamespacedKey(plugin, "adventure-rod")
    override val type = Material.BLAZE_ROD
    override val name = "Adventure Rod"
    override val nameStyle = Style.style(NamedTextColor.GOLD)
    override val unbreakable = true
    override val description = listOf<Component>()
    override val enchants: Map<Enchantment, Int> = mapOf(adventureEnchants.unique to 1)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (block != null) {
            val player = event.player
            val item = player.inventory.itemInMainHand
            if (isInstance(item)) {
                when (val state = block.state) {
                    is Chest -> checkBlockLootStatus(event, state, player)
                    is ShulkerBox -> checkBlockLootStatus(event, state, player)
                    is Barrel -> checkBlockLootStatus(event, state, player)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: PlayerInteractEntityEvent) {
        // Event fires once for each hand
        if (event.hand == EquipmentSlot.HAND) {
            val entity = event.rightClicked
            val player = event.player
            val item = player.inventory.itemInMainHand
            if (isInstance(item)) {
                when (entity) {
                    is StorageMinecart -> checkEntityLootStatus(event, entity, player)
                    is Lootable -> checkEntityLootStatus(event, entity, player)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        if (attacker is Player) {
            val entity = event.entity
            if (entity !is Player && entity !is StorageMinecart) {
                val item = attacker.inventory.itemInMainHand
                if (isInstance(item)) {
                    when (attacker.permissionValue(adventureRodPermission)) {
                        TriState.NOT_SET,
                        TriState.TRUE -> {
                            entity.remove()
                            event.isCancelled = true
                        }
                        TriState.FALSE -> {}
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: VehicleDamageEvent) {
        val attacker = event.attacker
        if (attacker is Player) {
            val entity = event.vehicle
            val item = attacker.inventory.itemInMainHand
            if (isInstance(item)) {
                when (attacker.permissionValue(adventureRodPermission)) {
                    TriState.NOT_SET,
                    TriState.TRUE -> {
                        when (entity) {
                            is StorageMinecart -> clearEntityLoot(event, entity, attacker)
                        }
                    }
                    TriState.FALSE -> {}
                }
            }
        }
    }

    // TODO: Needed for armor stands?
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    fun on(event: PlayerInteractAtEntityEvent) {
//    }

    private fun <S> checkBlockLootStatus(event: PlayerInteractEvent, state: S, player: Player) where S: InventoryHolder, S: Lootable {
        val lootTable = state.lootTable
        if (lootTable != null) {
            when (event.action) {
                Action.LEFT_CLICK_BLOCK -> {
                    when (player.permissionValue(adventureRodPermission)) {
                        TriState.NOT_SET,
                        TriState.TRUE -> {
                            state.inventory.clear()
                            player.sendMessage("Cleared loot")
                            event.isCancelled = true
                        }
                        TriState.FALSE -> {}
                    }
                }
                Action.RIGHT_CLICK_BLOCK -> {
                    when (player.permissionValue(adventureRodPermission)) {
                        TriState.NOT_SET,
                        TriState.TRUE -> {
                            val holder = state.inventory.holder as LootableInventory
                            val now = System.currentTimeMillis()
                            val remaining = (holder.nextRefill - now)
                            if (remaining <= 0) {
                                player.sendMessage("Loot ready: ${lootTable.key.asString()}")
                            } else {
                                player.sendMessage("Seconds until refill: ${remaining / 1000} ${lootTable.key.asString()}")
                            }
                            event.isCancelled = true
                        }
                        TriState.FALSE -> {}
                    }
                }
                else -> {}
            }
        }
    }

    private fun checkEntityLootStatus(event: PlayerInteractEntityEvent, entity: LootableEntityInventory, player: Player) {
        val lootTable = entity.lootTable
        if (lootTable != null) {
            when (player.permissionValue(adventureRodPermission)) {
                TriState.NOT_SET,
                TriState.TRUE -> {
                    val now = System.currentTimeMillis()
                    val remaining = (entity.nextRefill - now)
                    if (remaining <= 0) {
                        player.sendMessage("Loot ready: ${lootTable.key.asString()}")
                    } else {
                        player.sendMessage("Seconds until refill: ${remaining / 1000} ${lootTable.key.asString()}")
                    }
                    event.isCancelled = true
                }
                TriState.FALSE -> {}
            }
        }
    }

    private fun checkEntityLootStatus(event: PlayerInteractEntityEvent, entity: Lootable, player: Player) {
        val lootTable = entity.lootTable
        if (lootTable != null) {
            when (player.permissionValue(adventureRodPermission)) {
                TriState.NOT_SET,
                TriState.TRUE -> {
                    player.sendMessage("Loot ready: ${lootTable.key.asString()}")
                    event.isCancelled = true
                }
                TriState.FALSE -> {}
            }
        }
    }

    private fun <T> clearEntityLoot(event: VehicleDamageEvent, entity: T, player: Player) where T: InventoryHolder, T: LootableEntityInventory {
        val lootTable = entity.lootTable
        if (lootTable != null) {
            when (player.permissionValue(adventureRodPermission)) {
                TriState.NOT_SET,
                TriState.TRUE -> {
                    entity.inventory.clear()
                    entity.lootTable = lootTable
                    player.sendMessage("Cleared loot")
                    event.isCancelled = true
                }
                TriState.FALSE -> {}
            }
        }
    }
}
