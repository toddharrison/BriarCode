package com.briarcraft.adventure.api.item

import com.briarcraft.kotlin.util.itemStackOf
import net.kyori.adventure.util.TriState
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.Recipe

interface CraftableItem: ListenerItem {
    val recipe: Recipe
    val craftPermission: String
    val usePermission: String
    val prepareTrueCheck: (event: PrepareItemCraftEvent) -> Unit

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PrepareItemCraftEvent) {
        val recipe = event.recipe
        val recipeKey = if (recipe is Keyed) {
            recipe.key
        } else null

        if (key == recipeKey) {
            val player = event.viewers.first { it is Player } as Player
            when (player.permissionValue(craftPermission)) {
                TriState.NOT_SET,
                TriState.TRUE -> prepareTrueCheck(event)
                TriState.FALSE -> event.inventory.result = itemStackOf(Material.AIR)
            }
        }
    }
}
