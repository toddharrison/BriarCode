package com.briarcraft.rtw.util

import kotlinx.coroutines.delay
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

suspend fun forEntitiesInChunk(
    plugin: Plugin,
    chunk: Chunk,
    millisecondsBetweenLoadCheck: Long,
    ignoreEntityTypes: Set<EntityType> = setOf(),
    action: suspend (Sequence<Entity>) -> Unit
) {
    chunk.addPluginChunkTicket(plugin)

    while (!chunk.isEntitiesLoaded) {
        delay(millisecondsBetweenLoadCheck)
    }
    action(chunk.entities
        .asSequence()
        .filterNot { it is Player }
        .filter { !ignoreEntityTypes.contains(it.type) })

    chunk.removePluginChunkTicket(plugin)
}
