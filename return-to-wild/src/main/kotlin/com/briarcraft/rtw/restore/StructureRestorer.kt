package com.briarcraft.rtw.restore

import com.briarcraft.kotlin.util.getAllStartStructuresAt
import com.briarcraft.rtw.change.entity.EntityOriginRepository
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent

class StructureRestorer(
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

    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: ChunkLoadEvent) {
        val chunk = event.chunk
        val structures = getAllStartStructuresAt(allStructures, chunk.world, chunk.x, chunk.z)
        val location = Location(chunk.world, (chunk.x shl 4) + 7.5, 0.0, (chunk.z shl 4) + 7.5)

        if (structures.stream().anyMatch(oceanRuins::contains)) { restoreOceanRuins(location) }
        if (structures.stream().anyMatch(villages::contains)) { restoreVillage(location) }
//        if (structures.contains(mansion)) {}
        if (structures.contains(swampHut)) { restoreWitchHut(location) }
        if (structures.contains(endCity)) { restoreEndCity(location) }
        if (structures.contains(monument)) { restoreMonument(location) }
        if (structures.contains(igloo)) { restoreIgloo(location) }
        if (structures.contains(bastionRemnant)) { restoreBastion(location) }
        if (structures.contains(pillagerOutpost)) { restoreOutpost(location) }
    }

    private suspend fun restoreOceanRuins(loc: Location) {
        val searchRadius = 64
        val drowned = entityOriginRepo.find(EntityType.DROWNED, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${drowned.size} drowned for ocean ruins at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreVillage(loc: Location) {
        val searchRadius = 128
        val villagers = entityOriginRepo.find(EntityType.VILLAGER, loc, searchRadius)
        val zombieVillagers = entityOriginRepo.find(EntityType.ZOMBIE_VILLAGER, loc, searchRadius)
        val golems = entityOriginRepo.find(EntityType.IRON_GOLEM, loc, searchRadius)

        val foundVillagers = loc.world.getNearbyEntitiesByType(Villager::class.java, loc, searchRadius.toDouble()).size

        loc.world.sendMessage(Component.text("Found $foundVillagers villagers. Restored ${villagers.size} villagers, ${zombieVillagers.size} zombie villagers, and ${golems.size} iron golems for village at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreWitchHut(loc: Location) {
        val searchRadius = 32
        val witches = entityOriginRepo.find(EntityType.WITCH, loc, searchRadius)
        val cats = entityOriginRepo.find(EntityType.CAT, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${witches.size} witches and ${cats.size} cats for witch hut at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreEndCity(loc: Location) {
        val searchRadius = 64
        val shulkers = entityOriginRepo.find(EntityType.SHULKER, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${shulkers.size} shulkers for end city at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreMonument(loc: Location) {
        val searchRadius = 64
        val elderGuardians = entityOriginRepo.find(EntityType.ELDER_GUARDIAN, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${elderGuardians.size} elder guardians for monument at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreIgloo(loc: Location) {
        val searchRadius = 32
        val villagers = entityOriginRepo.find(EntityType.VILLAGER, loc, searchRadius)
        val zombieVillagers = entityOriginRepo.find(EntityType.ZOMBIE_VILLAGER, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${villagers.size} villagers and ${zombieVillagers.size} zombie villagers for igloo at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreBastion(loc: Location) {
        val searchRadius = 64
        val brutes = entityOriginRepo.find(EntityType.PIGLIN_BRUTE, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${brutes.size} piglin brutes for bastion at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }

    private suspend fun restoreOutpost(loc: Location) {
        val searchRadius = 64
        val pillagers = entityOriginRepo.find(EntityType.PILLAGER, loc, searchRadius)
        val allays = entityOriginRepo.find(EntityType.ALLAY, loc, searchRadius)
        val golems = entityOriginRepo.find(EntityType.IRON_GOLEM, loc, searchRadius)

        loc.world.sendMessage(Component.text("Restored ${pillagers.size} pillagers, ${allays.size} allays, and ${golems.size} iron golems for outpost at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}]"))
    }
}
