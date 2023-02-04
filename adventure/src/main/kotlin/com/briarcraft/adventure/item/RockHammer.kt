package com.briarcraft.adventure.item

import com.briarcraft.adventure.api.item.CraftableItem
import com.briarcraft.adventure.api.item.ListenerItem
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.util.TriState
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.Plugin

class RockHammer(plugin: Plugin): CraftableItem, ListenerItem {
    override val key = NamespacedKey(plugin, "rock-hammer")
    override val type = Material.GOLDEN_PICKAXE
    override val name = "Copper Rock Hammer"
    override val nameStyle = Style.style(NamedTextColor.GOLD)
    override val unbreakable = false
    override val description = listOf<Component>()
    override val enchants = mapOf<Enchantment, Int>()

    private val itemPermission = "briar.tool.rock-hammer"
    override val craftPermission = "$itemPermission.craft"
    override val usePermission = "$itemPermission.use"
    override val recipe = createRecipe()

    override val prepareTrueCheck = { _: PrepareItemCraftEvent -> }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    suspend fun on(event: BlockBreakEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (isInstance(item)) {
            val block = event.block
            val newType = when (block.type) {
                Material.STONE -> Material.COBBLESTONE
//                Material.STONE_SLAB -> Material.COBBLESTONE_SLAB
//                Material.STONE_STAIRS -> Material.COBBLESTONE_STAIRS
                Material.DEEPSLATE -> Material.COBBLED_DEEPSLATE
                Material.STONE_BRICKS -> Material.CRACKED_STONE_BRICKS
                Material.DEEPSLATE_TILES -> Material.CRACKED_DEEPSLATE_TILES
                Material.DEEPSLATE_BRICKS -> Material.CRACKED_DEEPSLATE_BRICKS
                Material.POLISHED_BLACKSTONE_BRICKS -> Material.CRACKED_POLISHED_BLACKSTONE_BRICKS
                Material.NETHER_BRICKS -> Material.CRACKED_NETHER_BRICKS
                else -> null
            }
            if (newType != null) {
                when (player.permissionValue(usePermission)) {
                    TriState.NOT_SET,
                    TriState.TRUE -> {
                        event.isDropItems = false
                        delay(1.ticks)
                        if (!event.isCancelled) {
                            val curBlock = block.location.block
                            // TODO Fix for partial blocks
//                            val state = curBlock.state
//                            val data = newType.createBlockData().merge(state.blockData)
//                            curBlock.setBlockData(data, false)
                            curBlock.setType(newType, true)
                        }
                    }
                    TriState.FALSE -> {}
                }
            }
        }
    }

    private fun createRecipe(): Recipe {
        val item = create()

        val recipe = ShapedRecipe(key, item)
        recipe.shape("CCC", " S ", " S ")
        recipe.setIngredient('C', Material.COPPER_INGOT)
        recipe.setIngredient('S', Material.STICK)
        return recipe
    }
}
