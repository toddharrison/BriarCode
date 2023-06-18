package com.briarcraft.regionscan

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.CreatureSpawner
import org.bukkit.block.data.Directional
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import java.util.EnumSet

@Suppress("unused")
class FeatureListener(private val plugin: RegionScanPlugin, private val action: suspend (FeatureData) -> Unit) : Listener {
    private val ignoreEntityTypes = EnumSet.of(EntityType.FALLING_BLOCK, EntityType.DROPPED_ITEM)
    private val delayTicks = 5L

    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun handleChunkLoadEvent(event: ChunkLoadEvent) {
        if (event.isNewChunk) {
            val chunk = event.chunk

            val tileEntities = chunk.tileEntities
                .asSequence()
                .map(::toTileEntityData)

            waitTillEntitiesLoaded(chunk, delayTicks) {
                val entities = chunk.entities
                    .asSequence()
                    .filter { e -> !ignoreEntityTypes.contains(e.type) }
                    .map(::toEntityData)

                for (entity in (tileEntities + entities)) {
                    action(entity)
                }
            }
        }
    }

    private suspend fun waitTillEntitiesLoaded(chunk: Chunk, delayTicks: Long, action: suspend () -> Unit) {
        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            plugin.launch {
                if (!chunk.isEntitiesLoaded) {
                    waitTillEntitiesLoaded(chunk, delayTicks, action)
                } else {
                    action()
                }
            }
        }, delayTicks)
    }

    private fun toTileEntityData(state: BlockState): FeatureData {
        val data = state.blockData
        val facing = if (data is Directional) {
            data.facing
        } else null

        val subKey = if (state is CreatureSpawner) {
            state.spawnedType?.key
        } else null

        return FeatureData("TileEntity", state.type.key, subKey, state.location, facing)
    }

    private fun toEntityData(entity: Entity): FeatureData {
        require(plugin.server.isPrimaryThread)

        val subKey = if (entity.passengers.isNotEmpty()) {
            entity.passengers.first().type.key
        } else null

        return FeatureData("Entity", entity.type.key, subKey, entity.location, entity.facing)
    }
}

data class FeatureData(
    val type: String,
    val key: NamespacedKey,
    val subKey: NamespacedKey?,
    val location: Location,
    val facing: BlockFace?,
)
