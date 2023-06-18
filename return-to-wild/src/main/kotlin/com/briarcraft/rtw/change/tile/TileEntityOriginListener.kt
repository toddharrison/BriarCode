package com.briarcraft.rtw.change.tile

import org.bukkit.Material
import org.bukkit.block.Banner
import org.bukkit.block.BrewingStand
import org.bukkit.block.CreatureSpawner
import org.bukkit.block.EntityBlockStorage
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.loot.Lootable

class TileEntityOriginListener(
    private val repository: TileEntityOriginRepository,
): Listener {
    @Suppress("DEPRECATION")
    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: ChunkLoadEvent) {
        if (event.isNewChunk) {
            val chunk = event.chunk
            chunk.tileEntities
                .asSequence()
                .map { state ->
                    val type = state.type
                    val location = state.location
                    val data = state.blockData.asString
                    val detail = when (state) {
                        is Lootable -> state.lootTable?.key?.asString()
                        is CreatureSpawner -> state.spawnedType?.key?.asString()
                        is BrewingStand -> state.inventory.asSequence()
                            .filterNotNull()
                            .filter { it.type == Material.POTION }
                            .map { it.itemMeta as PotionMeta }
                            .map { it.asString }
                            .joinToString()
                        is EntityBlockStorage<*> -> state.entityCount.toString()
                        is Sign -> state.lines.joinToString("\n")
                        is Banner -> state.baseColor.name + "," + state.patterns
                            .joinToString { "${it.color.name}:${it.pattern.identifier}" }
                        else -> null
                    }
                    TileEntityOrigin(type, location, data, detail)
                }
                .forEach { repository.save(it) }
        }
    }
}
