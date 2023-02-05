package com.briarcraft.rtw.change.entity

import com.briarcraft.rtw.util.forEntitiesInChunk
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.Chunk
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.loot.Lootable
import org.bukkit.plugin.Plugin

class EntityOriginListener(
    private val plugin: Plugin,
    private val repository: EntityOriginRepository,
    private val delay: Int = 1,
    private val ignoreEntityTypes: Set<EntityType> = setOf(EntityType.FALLING_BLOCK, EntityType.DROPPED_ITEM)
): Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: ChunkLoadEvent) {
        if (event.isNewChunk) {
            val chunk = event.chunk
            forEntitiesInChunk(plugin, chunk, delay.ticks, ignoreEntityTypes) { entities ->
                entities.map {
                    val type = it.type
                    val location = it.origin ?: it.location
                    val facing = when (it) {
                        is Shulker -> it.attachedFace
                        else -> it.facing
                    }
                    val passengers = it.passengers
                    val loot = when (it) {
                        is Lootable -> it.lootTable?.key
                        is ItemFrame -> it.item.type.key
                        else -> null
                    }
                    val detail = when (it) {
                        is ArmorStand -> getDetail(it)
                        else -> null
                    }
                    EntityOrigin(type, location, facing, passengers, loot, detail)
                }.forEach { repository.save(it) }
            }
        }
    }

    private fun getDetail(armorStand: ArmorStand): String {
        val equipment = armorStand.equipment
        return listOf(
            equipment.helmet.type,
            equipment.chestplate.type,
            equipment.leggings.type,
            equipment.boots.type,
            equipment.itemInMainHand.type,
            equipment.itemInOffHand.type
        )
            .filterNot { mat -> mat.isAir }
            .joinToString(",") { mat -> mat.key.asString() }
    }
}
