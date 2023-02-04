package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.item.CraftableItem
import com.briarcraft.adventure.api.item.ListenerItem
import com.briarcraft.kotlin.util.itemStackOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.util.TriState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Hopper
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.lang.IllegalArgumentException

class FilterHopper(plugin: Plugin): CraftableItem, ListenerItem {
    override val key = NamespacedKey(plugin, "filter-hopper")
    override val type = Material.HOPPER
    override val name = "Filter Hopper"
    override val nameStyle = Style.style(NamedTextColor.GOLD)
    override val unbreakable = true
    override val description = listOf<Component>()
    override val enchants = mapOf<Enchantment, Int>()

    private val itemPermission = "briar.tool.filter-hopper"
    override val craftPermission = "$itemPermission.craft"
    override val usePermission = "$itemPermission.use"
    override val recipe = createRecipe()

    override val prepareTrueCheck = { event: PrepareItemCraftEvent ->
        if (event.inventory.any { it != null && it.itemMeta.hasLore() }) {
            event.inventory.result = itemStackOf(Material.AIR)
        } else {
            val craftedItem = event.inventory.result
            if (craftedItem != null && !craftedItem.type.isAir) {
                val reagents = event.inventory.contents
                    .asSequence()
                    .drop(1)
                    .filterNotNull()
                    .map { it.type }
                    .filterNot { it.isAir }
                    .toList()
                if (reagents.size == 2) {
                    setHopperItemFilter(craftedItem, reagents.first())
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockPlaceEvent) {
        val player = event.player
        val placedItem = event.itemInHand
        val filter = getHopperItemFilter(placedItem)
        if (filter != null) {
            when (player.permissionValue(usePermission)) {
                TriState.NOT_SET,
                TriState.TRUE -> {
                    val state = event.blockPlaced.state
                    if (state is Hopper) {
                        setHopperFilter(state, filter)
                        player.sendMessage(Component.text("Placed a filter hopper for $filter"))
                    }
                }
                TriState.FALSE -> event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        if (block.type == Material.HOPPER) {
            val state = block.state as Hopper
            val filter = getHopperFilter(state)
            if (filter != null) {
                val location = block.location
                location.world.dropItemNaturally(location, itemStackOf(filter))
                player.sendMessage(Component.text("Broke a filter hopper for $filter"))
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: InventoryPickupItemEvent) {
        val holder = event.inventory.holder
        if (holder is Hopper) {
            val filter = getHopperFilter(holder)
            if (filter != null) {
                if (event.item.itemStack.type != filter) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: InventoryMoveItemEvent) {
        val holder = event.destination.holder
        if (holder is Hopper) {
            val filter = getHopperFilter(holder)
            if (filter != null) {
                if (event.item.type != filter) {
                    event.isCancelled = true

                    val source = event.source
                    val destination = event.destination
                    val amount = event.item.amount
                    val amountAvailable = source.all(filter).values.sumOf { it.amount }
                    if (amountAvailable > 0) {
                        val item = itemStackOf(filter, amount = if (amountAvailable >= amount) amount else amountAvailable)
                        emitInventoryMoveItemEvent(source, item, destination, event.initiator == source)
                    }
                }
            }
        }
    }

    private fun createRecipe(): Recipe {
        val item = create()

        val recipe = ShapedRecipe(key, item)
        recipe.shape("I", "H")
        recipe.setIngredient('I', RecipeChoice.MaterialChoice(
            Material.values()
                .asList()
                .filter { it.isItem }
                .filterNot { it == Material.AIR }
        ))
        recipe.setIngredient('H', Material.HOPPER)
        return recipe
    }

    private fun getHopperItemFilter(item: ItemStack): Material? {
        return if (item.type == Material.HOPPER) {
            val meta = item.itemMeta
            try {
                val filter = meta.persistentDataContainer.get(key, PersistentDataType.STRING)
                if (filter != null) {
                    Material.matchMaterial(filter)
                } else null
            } catch (e: IllegalArgumentException) { null }
        } else null
    }

    private fun setHopperItemFilter(item: ItemStack, material: Material) {
        require(item.type == Material.HOPPER)
        val meta = item.itemMeta
        meta.persistentDataContainer.set(key, PersistentDataType.STRING, material.key.asString())
        meta.lore(listOf(Component.text(material.key.asString())))
        item.itemMeta = meta
    }

    private fun getHopperFilter(hopper: Hopper): Material? {
        return try {
            val filterString = hopper.persistentDataContainer.get(key, PersistentDataType.STRING)
            if (filterString != null) {
                Material.matchMaterial(filterString)
            } else null
        } catch (e: IllegalArgumentException) { null }
    }

    private fun setHopperFilter(hopper: Hopper, material: Material) {
        hopper.customName(Component.text("Filter Hopper ($material)"))
        hopper.persistentDataContainer.set(key, PersistentDataType.STRING, material.key.asString())
        hopper.update()
    }

    private fun emitInventoryMoveItemEvent(source: Inventory, item: ItemStack, destination: Inventory, didSourceInitiate: Boolean) {
        val newEvent = InventoryMoveItemEvent(source, item, destination, didSourceInitiate)
        Bukkit.getPluginManager().callEvent(newEvent)
        if (!newEvent.isCancelled) {
            source.removeItem(newEvent.item)
            val remainder = destination.addItem(newEvent.item)
            if (remainder.size == 1) {
                source.addItem(remainder.values.first())
            }
        }
    }
}
