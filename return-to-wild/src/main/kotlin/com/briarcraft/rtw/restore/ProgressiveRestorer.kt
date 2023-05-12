package com.briarcraft.rtw.restore

import com.briarcraft.rtw.category.*
import com.briarcraft.rtw.change.block.*
import com.briarcraft.rtw.config.RestorerConfig
import com.briarcraft.rtw.perm.PermissionService
import com.briarcraft.rtw.util.AtomicToggle
import com.briarcraft.rtw.util.CONTEXT_ORIGINAL
import com.briarcraft.rtw.util.processByChunk
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class ProgressiveRestorer(
    override val plugin: SuspendingJavaPlugin,
    override val blockChangeRepo: BlockChangeRepository2,
    override val permService: PermissionService,
    override val pauseFlag: AtomicToggle,
    private val restorerConfig: RestorerConfig,
): Restorer {
    private val logger = plugin.logger

    override suspend fun start() {
        restoreGrief(restorerConfig.griefCheckDelay, restorerConfig.minAgeOfGrief)
        restorePlayers(restorerConfig.playerCheckDelay)
    }

    private suspend fun restoreGrief(checkDelay: Duration, minAgeOfGrief: Duration) {
        plugin.launch {
            while (true) {
                try {
                    if (pauseFlag.get()) { logger.info("RTW grief restore paused") }

                    delay(checkDelay)

                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.ENDERMAN.key, minAgeOfGrief) { "RTW restoring ${it.size} enderman steals" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.CREEPER.key, minAgeOfGrief) { "RTW restoring ${it.size} creeper explosions" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.FIREBALL.key, minAgeOfGrief) { "RTW restoring ${it.size} fireball explosions" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.WITHER.key, minAgeOfGrief) { "RTW restoring ${it.size} wither explosions" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.WITHER_SKULL.key, minAgeOfGrief) { "RTW restoring ${it.size} wither skull explosions" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.ENDER_CRYSTAL.key, minAgeOfGrief) { "RTW restoring ${it.size} ender crystal explosions" }
                    if (pauseFlag.get()) continue
                    restoreGrief(EntityType.SILVERFISH.key, minAgeOfGrief) { "RTW restoring ${it.size} silverfish infestations" }
                } catch (e: Exception) {
                    println("ERROR A")
                    break
                }
            }
        }
    }

    private suspend fun restorePlayers(checkDelay: Duration) {
        plugin.launch {
            while (true) {
                try {
                    if (pauseFlag.get()) { logger.info("RTW player restore paused") }

                    println("WAITING")
                    delay(checkDelay)

                    // New materials go to air
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_ILLEGAL, restorerConfig.illegalToAir) { "RTW removing ${it.size} illegal blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_PERISHABLE, restorerConfig.perishableToAir) { "RTW removing ${it.size} perishable blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_LIQUID, restorerConfig.liquidToAir) { "RTW removing ${it.size} liquid blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_PLANT, restorerConfig.plantToAir) { "RTW removing ${it.size} plant blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_SOIL, restorerConfig.soilToAir) { "RTW removing ${it.size} soil blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_SPONGY, restorerConfig.spongyToAir) { "RTW removing ${it.size} spongy blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_FIBER, restorerConfig.fiberToAir) { "RTW removing ${it.size} fiber blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_WOOD, restorerConfig.woodToAir) { "RTW removing ${it.size} wood blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_LOG, restorerConfig.logToAir) { "RTW removing ${it.size} log blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_GLASS, restorerConfig.glassToAir) { "RTW removing ${it.size} glass blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_SOFT_STONE, restorerConfig.softStoneToAir) { "RTW removing ${it.size} soft stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_STONE, restorerConfig.stoneToAir) { "RTW removing ${it.size} stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_HARD_STONE, restorerConfig.hardStoneToAir) { "RTW removing ${it.size} hard stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_ORE, restorerConfig.oreToAir) { "RTW removing ${it.size} ore blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_METAL, restorerConfig.metalToAir) { "RTW removing ${it.size} metal blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_MAGIC, restorerConfig.magicToAir) { "RTW removing ${it.size} magic blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_GEM, restorerConfig.gemToAir) { "RTW removing ${it.size} gem blocks" }
                    if (pauseFlag.get()) continue
                    restoreToAir(CATEGORY_CONTAINER, restorerConfig.containerToAir) { "RTW removing ${it.size} container blocks" }

                    // Air goes to old materials
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_SOIL, restorerConfig.airToSoil) { "RTW restoring ${it.size} soil blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_LOG, restorerConfig.airToLog) { "RTW restoring ${it.size} log blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_WOOD, restorerConfig.airToWood) { "RTW restoring ${it.size} wood blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_FIBER, restorerConfig.airToFiber) { "RTW restoring ${it.size} fiber blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_SPONGY, restorerConfig.airToSpongy) { "RTW restoring ${it.size} spongy blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_PLANT, restorerConfig.airToPlant) { "RTW restoring ${it.size} plant blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_SOFT_STONE, restorerConfig.airToSoftStone) { "RTW restoring ${it.size} soft stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_STONE, restorerConfig.airToStone) { "RTW restoring ${it.size} stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_HARD_STONE, restorerConfig.airToHardStone) { "RTW restoring ${it.size} hard stone blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_GLASS, restorerConfig.airToGlass) { "RTW restoring ${it.size} glass blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_LIQUID, restorerConfig.airToLiquid) { "RTW restoring ${it.size} liquid blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_ORE, restorerConfig.airToOre) { "RTW restoring ${it.size} ore blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_METAL, restorerConfig.airToMetal) { "RTW restoring ${it.size} metal blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_GEM, restorerConfig.airToGem) { "RTW restoring ${it.size} gem blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_CONTAINER, restorerConfig.airToContainer) { "RTW restoring ${it.size} container blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_MAGIC, restorerConfig.airToMagic) { "RTW restoring ${it.size} magic blocks" }
                    if (pauseFlag.get()) continue
                    restoreToOriginal(CATEGORY_PERISHABLE, restorerConfig.airToPerishable) { "RTW restoring ${it.size} perishable blocks" }
                } catch (e: Exception) {
                    println("ERROR B")
                    break
                }
            }
        }
    }

    private suspend fun restoreGrief(
        cause: NamespacedKey,
        minAge: Duration,
        context: String = CONTEXT_ORIGINAL,
        applyPhysics: Boolean = false,
        logMessage: (List<BlockChange>) -> String
    ) {
        blockChangeRepo.findChanges(context, cause, Instant.now().minus(minAge.toJavaDuration()))
            .filter { permService.isRestorable(it.location) }
            .also { if (it.isNotEmpty()) logger.info(logMessage(it)) }
            .processByChunk(plugin) { change ->
                change.location.block.setBlockData(change.blockData, applyPhysics)
                blockChangeRepo.deleteQueued(change)
            }
    }

    private suspend fun restoreToAir(
        newCategory: Int,
        minAge: Duration,
        context: String = CONTEXT_ORIGINAL,
        applyPhysics: Boolean = false,
        logMessage: (List<BlockChange>) -> String
    ) {
        blockChangeRepo.findByNewCategory(context, newCategory, Instant.now().minus(minAge.toJavaDuration()))
            .filter { permService.isRestorable(it.location) }
            .also { if (it.isNotEmpty()) logger.info(logMessage(it)) }
            .processByChunk(plugin) { change ->
                change.location.block.setBlockData(Bukkit.createBlockData(Material.AIR), applyPhysics)
                if (change.category == CATEGORY_GAS) {
                    blockChangeRepo.deleteQueued(change)
                } else {
                    blockChangeRepo.updateQueued(change, mapOf(
                        UPDATE_NEW_MATERIAL to Material.AIR,
                        UPDATE_NEW_CATEGORY to CATEGORY_GAS
                    ))
                }
            }
    }

    private suspend fun restoreToOriginal(
        category: Int,
        minAge: Duration,
        context: String = CONTEXT_ORIGINAL,
        applyPhysics: Boolean = false,
        logMessage: (List<BlockChange>) -> String
    ) {
        blockChangeRepo.findByCategories(context, category, CATEGORY_GAS, Instant.now().minus(minAge.toJavaDuration()))
            .filter { permService.isRestorable(it.location) }
            .also { if (it.isNotEmpty()) logger.info(logMessage(it)) }
            .processByChunk(plugin) { change ->
                change.location.block.setBlockData(change.blockData, applyPhysics)
                blockChangeRepo.deleteQueued(change)
            }
    }
}