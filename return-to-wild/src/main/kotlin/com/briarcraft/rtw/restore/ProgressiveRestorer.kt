package com.briarcraft.rtw.restore

import com.briarcraft.rtw.category.*
import com.briarcraft.rtw.change.block.BlockChange
import com.briarcraft.rtw.change.block.BlockChangeRepository
import com.briarcraft.rtw.change.block.UPDATE_NEW_CATEGORY
import com.briarcraft.rtw.change.block.UPDATE_NEW_MATERIAL
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
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class ProgressiveRestorer(
    override val plugin: SuspendingJavaPlugin,
    override val blockChangeRepo: BlockChangeRepository,
    override val permService: PermissionService,
    override val pauseFlag: AtomicToggle
): Restorer {
    private val logger = plugin.logger

    override suspend fun start() {
        restoreGrief(5.minutes, minAgeOfGrief = 3.hours)
        restorePlayers(5.minutes)
    }

    private suspend fun restoreGrief(checkDelay: Duration, minAgeOfGrief: Duration) {
        plugin.launch {
            while (true) {
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
            }
        }
    }

    private suspend fun restorePlayers(checkDelay: Duration) {
        plugin.launch {
            while (true) {
                if (pauseFlag.get()) { logger.info("RTW player restore paused") }

                delay(checkDelay)

                // New materials go to air
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_ILLEGAL, 0.minutes) { "RTW removing ${it.size} illegal blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_PERISHABLE, 1.days) { "RTW removing ${it.size} perishable blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_LIQUID, 2.days) { "RTW removing ${it.size} liquid blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_PLANT, 3.days) { "RTW removing ${it.size} plant blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_SOIL, 4.days) { "RTW removing ${it.size} soil blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_SPONGY, 5.days) { "RTW removing ${it.size} spongy blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_FIBER, 6.days) { "RTW removing ${it.size} fiber blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_WOOD, 7.days) { "RTW removing ${it.size} wood blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_LOG, 9.days) { "RTW removing ${it.size} log blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_GLASS, 11.days) { "RTW removing ${it.size} glass blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_SOFT_STONE, 13.days) { "RTW removing ${it.size} soft stone blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_STONE, 15.days) { "RTW removing ${it.size} stone blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_HARD_STONE, 17.days) { "RTW removing ${it.size} hard stone blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_ORE, 20.days) { "RTW removing ${it.size} ore blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_METAL, 23.days) { "RTW removing ${it.size} metal blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_MAGIC, 26.days) { "RTW removing ${it.size} magic blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_GEM, 29.days) { "RTW removing ${it.size} gem blocks" }
                if (pauseFlag.get()) continue
                restoreToAir(CATEGORY_CONTAINER, 30.days) { "RTW removing ${it.size} container blocks" }

                // Air goes to old materials
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_SOIL, 3.days) { "RTW restoring ${it.size} soil blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_LOG, 5.days) { "RTW restoring ${it.size} log blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_WOOD, 7.days) { "RTW restoring ${it.size} wood blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_FIBER, 9.days) { "RTW restoring ${it.size} fiber blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_SPONGY, 11.days) { "RTW restoring ${it.size} spongy blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_PLANT, 13.days) { "RTW restoring ${it.size} plant blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_SOFT_STONE, 15.days) { "RTW restoring ${it.size} soft stone blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_STONE, 17.days) { "RTW restoring ${it.size} stone blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_HARD_STONE, 19.days) { "RTW restoring ${it.size} hard stone blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_GLASS, 21.days) { "RTW restoring ${it.size} glass blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_LIQUID, 23.days) { "RTW restoring ${it.size} liquid blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_ORE, 25.days) { "RTW restoring ${it.size} ore blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_METAL, 27.days) { "RTW restoring ${it.size} metal blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_GEM, 29.days) { "RTW restoring ${it.size} gem blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_CONTAINER, 31.days) { "RTW restoring ${it.size} container blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_MAGIC, 33.days) { "RTW restoring ${it.size} magic blocks" }
                if (pauseFlag.get()) continue
                restoreToOriginal(CATEGORY_PERISHABLE, 35.days) { "RTW restoring ${it.size} perishable blocks" }
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