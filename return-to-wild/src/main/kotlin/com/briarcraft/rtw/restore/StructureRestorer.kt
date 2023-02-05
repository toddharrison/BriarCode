package com.briarcraft.rtw.restore

import com.briarcraft.kotlin.util.getAllStartStructuresAt
import com.briarcraft.kotlin.util.isStructureAt
import com.briarcraft.rtw.change.entity.EntityOriginRepository
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Allay
import org.bukkit.entity.Cat
import org.bukkit.entity.Drowned
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.IronGolem
import org.bukkit.entity.PiglinBrute
import org.bukkit.entity.Pillager
import org.bukkit.entity.Shulker
import org.bukkit.entity.Villager
import org.bukkit.entity.Witch
import org.bukkit.entity.ZombieVillager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.plugin.Plugin
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.min

class StructureRestorer(
    private val plugin: Plugin,
    private val entityOriginRepo: EntityOriginRepository,
): Listener {
    private val oceanRuinCold = NamespacedKey.minecraft("ocean_ruin_cold")
    private val oceanRuinWarm = NamespacedKey.minecraft("ocean_ruin_warm")
    private val oceanRuins = setOf(oceanRuinCold, oceanRuinWarm)

    private val villageSavanna = NamespacedKey.minecraft("village_savanna")
    private val villageSnowy = NamespacedKey.minecraft("village_snowy")
    private val villageTaiga = NamespacedKey.minecraft("village_taiga")
    private val villageDesert = NamespacedKey.minecraft("village_desert")
    private val villagePlains = NamespacedKey.minecraft("village_plains")
    private val villages = setOf(villageSavanna, villageSnowy, villageTaiga, villageDesert, villagePlains)

    private val mansion = NamespacedKey.minecraft("mansion")
    private val swampHut = NamespacedKey.minecraft("swamp_hut")
    private val endCity = NamespacedKey.minecraft("end_city")
    private val monument = NamespacedKey.minecraft("monument")
    private val igloo = NamespacedKey.minecraft("igloo")
    private val bastionRemnant = NamespacedKey.minecraft("bastion_remnant")
    private val pillagerOutpost = NamespacedKey.minecraft("pillager_outpost")

    private val allStructures = setOf(
        oceanRuinCold, oceanRuinWarm,
        villageSavanna, villageSnowy, villageTaiga, villageDesert, villagePlains,
        mansion, swampHut, endCity, monument, monument, igloo, bastionRemnant, pillagerOutpost
    )

    @Suppress("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: ChunkLoadEvent) {
        val chunk = event.chunk
        while (!chunk.isLoaded) {
            delay(1.ticks)
        }
        val structures = getAllStartStructuresAt(allStructures, event.world, chunk.x, chunk.z)
        if (structures != null) {
            oceanRuins.forEach { if (structures.contains(it)) restoreOceanRuins(chunk, it) }
            villages.forEach { if (structures.contains(it)) restoreVillage(chunk, it) }
//            if (structures.contains(mansion)) {}
            if (structures.contains(swampHut)) { restoreWitchHut(chunk) }
            if (structures.contains(endCity)) { restoreEndCity(chunk) }
            if (structures.contains(monument)) { restoreMonument(chunk) }
            if (structures.contains(igloo)) { restoreIgloo(chunk) }
            if (structures.contains(bastionRemnant)) { restoreBastion(chunk) }
            if (structures.contains(pillagerOutpost)) { restoreOutpost(chunk) }
        }
    }

    private suspend fun restoreOceanRuins(chunk: Chunk, oceanRuinsKey: NamespacedKey) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 64
        val chunks = getStructureChunks(plugin, oceanRuinsKey, chunk)
        spawnMissingEntities(chunk, EntityType.DROWNED, Drowned::class.java, 3, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreVillage(chunk: Chunk, villageKey: NamespacedKey) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 128
        val chunks = getStructureChunks(plugin, villageKey, chunk)
        spawnMissingEntities(chunk, EntityType.VILLAGER, Villager::class.java, 2, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.ZOMBIE_VILLAGER, ZombieVillager::class.java, 2, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.IRON_GOLEM, IronGolem::class.java, 1, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreWitchHut(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 32
        val chunks = getStructureChunks(plugin, swampHut, chunk)
        spawnMissingEntities(chunk, EntityType.WITCH, Witch::class.java, 1, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.CAT, Cat::class.java, 1, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreEndCity(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 64
        val chunks = getStructureChunks(plugin, endCity, chunk)
        spawnMissingEntities(chunk, EntityType.SHULKER, Shulker::class.java, 16, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreMonument(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 64
        val chunks = getStructureChunks(plugin, monument, chunk)
        spawnMissingEntities(chunk, EntityType.ELDER_GUARDIAN, ElderGuardian::class.java, 3, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreIgloo(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 32
        val chunks = getStructureChunks(plugin, igloo, chunk)
        spawnMissingEntities(chunk, EntityType.VILLAGER, Villager::class.java, 1, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.ZOMBIE_VILLAGER, ZombieVillager::class.java, 1, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreBastion(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 64
        val chunks = getStructureChunks(plugin, bastionRemnant, chunk)
        spawnMissingEntities(chunk, EntityType.PIGLIN_BRUTE, PiglinBrute::class.java, 3, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private suspend fun restoreOutpost(chunk: Chunk) {
        val before = Instant.now().minus(5, ChronoUnit.MINUTES)
        val searchRadius = 64
        val chunks = getStructureChunks(plugin, pillagerOutpost, chunk)
        spawnMissingEntities(chunk, EntityType.PILLAGER, Pillager::class.java, 8, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.ALLAY, Allay::class.java, 3, searchRadius, before)
        spawnMissingEntities(chunk, EntityType.IRON_GOLEM, IronGolem::class.java, 1, searchRadius, before)
        releaseTickets(plugin, chunks)
    }

    private fun getStructureChunks(plugin: Plugin, structureKey: NamespacedKey, chunk: Chunk): Set<Chunk> {
        val world = chunk.world
        val chunkX = chunk.x
        val chunkZ = chunk.z
        return when (isStructureAt(structureKey, world, chunkX, chunkZ)) {
            true -> {
                chunk.addPluginChunkTicket(plugin)
                setOf(chunk) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX + 1, chunkZ)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX, chunkZ + 1)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX - 1, chunkZ)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX, chunkZ - 1)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX + 1, chunkZ + 1)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX - 1, chunkZ - 1)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX + 1, chunkZ - 1)) +
                        getStructureChunks(plugin, structureKey, world.getChunkAt(chunkX - 1, chunkZ + 1))
            }
            false -> setOf()
            null -> {
                plugin.logger.warning("Failure checking if structure is at chunk: $chunk")
                setOf()
            }
        }
    }

    private suspend fun <T: Entity> spawnMissingEntities(
        chunk: Chunk,
        entityType: EntityType,
        entityClass: Class<T>,
        maxEntities: Int,
        searchRadius: Int,
        before: Instant
    ) {
        val centerLoc = getCenterOfChunk(chunk)
        val entities = entityOriginRepo.find(entityType, centerLoc, searchRadius, before)
        if (entities.isNotEmpty()) {
            val foundEntities = chunk.world.getNearbyEntitiesByType(entityClass, centerLoc, searchRadius.toDouble()).size
            val entitiesToCreate = min(entities.size - foundEntities, maxEntities)
            if (entitiesToCreate > 0) {
                entities.asSequence().shuffled().take(entitiesToCreate).forEach {
                    chunk.world.spawn(it.location, entityClass)
                    entityOriginRepo.updateTimestampToNow(it)
                }
                plugin.logger.info("Restored $entitiesToCreate $entityType at [${chunk.x}, ${chunk.z}]")
            }
        }
    }

    private fun getCenterOfChunk(chunk: Chunk) = Location(chunk.world, (chunk.x shl 4) + 7.5, 0.0, (chunk.z shl 4) + 7.5)

    private fun releaseTickets(plugin: Plugin, chunks: Set<Chunk>) = chunks.forEach { it.removePluginChunkTicket(plugin) }
}
