package com.briarcraft.regionscan

import org.bukkit.Chunk
import org.bukkit.block.BlockState
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

class ChunkScanner(private val plugin: JavaPlugin) {
    fun scan(
        chunk: Chunk,
        tileEntitiesFilter: (BlockState) -> Boolean,
        entitiesFilter: (Entity) -> Boolean,
        action: (List<BlockState>, List<Entity>) -> Unit
    ): CompletableFuture<Void> {
        try {
            val future = CompletableFuture<Void>()

            plugin.server.scheduler.runTask(plugin) { ->
                require(plugin.server.isPrimaryThread)

                // Load and reserve chunk
                chunk.addPluginChunkTicket(plugin)

                val tileEntities = chunk.tileEntities.filter(tileEntitiesFilter)
                waitTillEntitiesLoaded(chunk, 5) {
                    val entities = chunk.entities.filter(entitiesFilter)
                    if (tileEntities.isNotEmpty() || entities.isNotEmpty()) action(tileEntities, entities)

                    // Processing complete, release chunk
                    chunk.removePluginChunkTicket(plugin)

                    future.complete(null)
                }
            }

            return future
        } catch (e: Throwable) {
            plugin.logger.log(Level.WARNING, "Error scanning chunk", e)
            throw e
        }
    }

    private fun waitTillEntitiesLoaded(chunk: Chunk, delayTicks: Long, action: () -> Unit) {
        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            if (!chunk.isEntitiesLoaded) {
                if (delayTicks.toInt() % 10 == 0) plugin.logger.info("Waiting for entities for chunk $chunk...")
                waitTillEntitiesLoaded(chunk, delayTicks + 1, action)
            } else {
                action()
            }
        }, delayTicks)
    }
}
