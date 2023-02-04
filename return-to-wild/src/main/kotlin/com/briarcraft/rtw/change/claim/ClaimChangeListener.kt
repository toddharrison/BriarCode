package com.briarcraft.rtw.change.claim

import com.briarcraft.rtw.change.block.BlockChangeRepository
import com.briarcraft.rtw.change.block.UPDATE_TIMESTAMP
import com.briarcraft.rtw.util.CONTEXT_ORIGINAL
import dev.espi.protectionstones.event.PSCreateEvent
import dev.espi.protectionstones.event.PSRemoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.time.Instant
import java.util.logging.Logger

class ClaimChangeListener(private val logger: Logger, private val blockChangeRepo: BlockChangeRepository): Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun on(event: PSCreateEvent) {
        logger.info("Player ${event.player.name} placed claim")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: PSRemoveEvent) {
        logger.info("Player ${event.player.name} removed claim")
        val wgRegion = event.region.wgRegion
        val minPoint = wgRegion.minimumPoint
        val maxPoint = wgRegion.maximumPoint

        blockChangeRepo.findByRegion(
            CONTEXT_ORIGINAL,
            event.region.world,
            minX = minPoint.x,
            maxX = maxPoint.x,
            minY = minPoint.y,
            maxY = maxPoint.y,
            minZ = minPoint.z,
            maxZ = maxPoint.z
        )
            .filter { wgRegion.contains(it.location.blockX, it.location.blockY, it.location.blockZ) }
            .forEach { change ->
                blockChangeRepo.updateQueued(change, mapOf(UPDATE_TIMESTAMP to Instant.now()))
            }

        logger.info("Reset timestamps on block changes in removed region")
    }
}