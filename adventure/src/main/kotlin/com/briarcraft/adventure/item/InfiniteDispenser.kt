package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.BYTE_FALSE
import com.briarcraft.adventure.api.BYTE_TRUE
import com.briarcraft.adventure.api.item.CraftableItem
import com.briarcraft.kotlin.util.itemStackOf
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Dispenser
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class InfiniteDispenser(plugin: Plugin): CraftableItem {
    override val key = NamespacedKey(plugin, "infinite-dispenser")
    override val type = Material.DISPENSER
    override val name = "Infinite Dispenser"
    override val nameStyle = Style.style(NamedTextColor.GOLD)
    override val unbreakable = false
    override val description = listOf<Component>()
    override val enchants = mapOf<Enchantment, Int>()

    private val itemPermission = "briar.adventure.infinite-dispenser"
    override val craftPermission = "$itemPermission.craft"
    override val usePermission = "$itemPermission.open"
    override val recipe = createRecipe()

    override val prepareTrueCheck = { event: PrepareItemCraftEvent ->
        if (event.inventory.any { it != null && it.itemMeta.hasLore() }) {
            event.inventory.result = itemStackOf(Material.AIR)
        } else {
            val recipe = event.recipe
            if (recipe is ShapelessRecipe && recipe.key == key) {
                val player = event.viewers.first { it is Player } as Player
                when (player.permissionValue(craftPermission)) {
                    TriState.NOT_SET,
                    TriState.TRUE -> {
                        val craftedItem = event.inventory.result
                        if (craftedItem != null) setAsInfinite(craftedItem)
                    }
                    TriState.FALSE -> event.inventory.result = itemStackOf(Material.AIR)
                }
            }
        }
    }

    private fun createRecipe(): Recipe {
        val item = create()

        val recipe = ShapelessRecipe(key, item)
        recipe.addIngredient(Material.DISPENSER)
        recipe.addIngredient(Material.TOTEM_OF_UNDYING)
        return recipe
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockPlaceEvent) {
        val player = event.player
        val placedItem = event.itemInHand
        val state = event.blockPlaced.state
        if (state is Dispenser) {
            if (isInfinite(placedItem)) {
                setAsInfinite(state, true)
                player.sendMessage(Component.text("Placed an infinite dispenser"))
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: InventoryOpenEvent) {
        val holder = event.inventory.holder
        if (holder is Dispenser && isInfinite(holder)) {
            val player = event.player
            if (player.permissionValue(usePermission) == TriState.FALSE) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: InventoryMoveItemEvent) {
        val holder = event.destination.holder
        if (holder is Dispenser && isInfinite(holder)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val state = block.state
        if (state is Dispenser) {
            if (isInfinite(state)) {
                val location = block.location
                location.world.dropItemNaturally(location, itemStackOf(Material.TOTEM_OF_UNDYING))
                player.sendMessage(Component.text("Broke an infinite dispenser"))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    suspend fun on(event: BlockDispenseEvent) {
        val block = event.block
        val state = block.state
        if (state is Dispenser && isInfinite(state)) {
            val itemCopy = event.item.clone()
            delay(1.ticks)
            state.inventory.addItem(itemCopy)
        }
    }

    private fun isInfinite(item: ItemStack): Boolean {
        return if (item.type == Material.DISPENSER) {
            item.itemMeta.persistentDataContainer.get(key, PersistentDataType.BYTE) == BYTE_TRUE
        } else false
    }

    private fun setAsInfinite(item: ItemStack) {
        require(item.type == Material.DISPENSER)
        val meta = item.itemMeta
        meta.persistentDataContainer.set(key, PersistentDataType.BYTE, BYTE_TRUE)
        item.itemMeta = meta
    }

    private fun isInfinite(dispenser: Dispenser): Boolean {
        return when (dispenser.persistentDataContainer.get(key, PersistentDataType.BYTE)) {
            BYTE_TRUE -> true
            BYTE_FALSE -> false
            else -> false
        }
    }

    @Suppress("SameParameterValue")
    private fun setAsInfinite(dispenser: Dispenser, isInfinite: Boolean) {
        if (isInfinite) {
            dispenser.customName(Component.text("Infinite Dispenser"))
            dispenser.persistentDataContainer.set(key, PersistentDataType.BYTE, BYTE_TRUE)
            dispenser.update()
        } else {
            dispenser.customName(null)
            dispenser.persistentDataContainer.remove(key)
            dispenser.update()
        }
    }
}
