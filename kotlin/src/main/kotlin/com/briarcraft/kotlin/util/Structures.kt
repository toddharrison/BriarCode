package com.briarcraft.kotlin.util

import net.minecraft.core.Holder
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.StructureCheckResult
import org.bukkit.*
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld
import org.bukkit.craftbukkit.v1_20_R1.generator.structure.CraftStructure
import org.bukkit.generator.structure.StructureType

val structureByKey = Registry.STRUCTURE.associateBy { it.key }

fun isStructureAt(structureType: StructureType, world: World, chunkX: Int, chunkZ: Int): Boolean? {
    return isStructureAt(structureType.key, world, chunkX, chunkZ)
}

fun isStructureAt(structureKey: NamespacedKey, world: World, chunkX: Int, chunkZ: Int): Boolean? {
    val structureManager = (world as CraftWorld).handle.structureManager()
    val chunkPos = ChunkPos(chunkX, chunkZ)
    return structureByKey[structureKey]?.let { structure ->
        val holder = Holder.direct(CraftStructure.bukkitToMinecraft(structure))
        when (structureManager.checkStructurePresence(chunkPos, holder.value(), false)) {
            StructureCheckResult.START_PRESENT -> true
            StructureCheckResult.START_NOT_PRESENT -> false
            StructureCheckResult.CHUNK_LOAD_NEEDED -> null
            else -> null
        }
    }
}

fun getAllStartStructuresAt(world: World, chunkX: Int, chunkZ: Int): Set<NamespacedKey>? {
    val structureManager = (world as CraftWorld).handle.structureManager()
    val chunkPos = ChunkPos(chunkX, chunkZ)
    return Registry.STRUCTURE.mapNotNull { structure ->
        val holder = Holder.direct(CraftStructure.bukkitToMinecraft(structure))
        when (structureManager.checkStructurePresence(chunkPos, holder.value(), false)) {
            StructureCheckResult.START_PRESENT -> structure.key
            StructureCheckResult.CHUNK_LOAD_NEEDED -> return@getAllStartStructuresAt null
            else -> null
        }
    }.toSet()
}

fun getAllStartStructuresAt(structureKeys: Set<NamespacedKey>, world: World, chunkX: Int, chunkZ: Int): Set<NamespacedKey>? {
    val structureManager = (world as CraftWorld).handle.structureManager()
    val chunkPos = ChunkPos(chunkX, chunkZ)
    return structureKeys.mapNotNull { structureByKey[it] }.mapNotNull { structure ->
        val holder = Holder.direct(CraftStructure.bukkitToMinecraft(structure))
        when (structureManager.checkStructurePresence(chunkPos, holder.value(), false)) {
            StructureCheckResult.START_PRESENT -> structure.key
            StructureCheckResult.CHUNK_LOAD_NEEDED -> return@getAllStartStructuresAt null
            else -> null
        }
    }.toSet()
}

fun getAllStructuresAt(world: World, chunkX: Int, chunkZ: Int): Set<NamespacedKey> {
    val cWorld = world as CraftWorld
    val registryAccess = cWorld.handle.registryAccess()
    val structureManager = cWorld.handle.structureManager()
    val blockPos = ChunkPos(chunkX, chunkZ).worldPosition
    return structureManager.getAllStructuresAt(blockPos)
        .mapKeys { CraftStructure.minecraftToBukkit(it.key, registryAccess).key }
        .keys
}
