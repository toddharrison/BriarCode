package com.briarcraft.rtw

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.change.block.BlockChangeConfig
import com.briarcraft.rtw.change.block.BlockChangeListener
import com.briarcraft.rtw.change.block.BlockChangeRepository
import com.briarcraft.rtw.change.block.BlockChangeRepository2
import com.briarcraft.rtw.change.claim.ClaimChangeListener
import com.briarcraft.rtw.change.entity.EntityOriginListener
import com.briarcraft.rtw.change.entity.EntityOriginRepository
import com.briarcraft.rtw.change.player.PlayerListener
import com.briarcraft.rtw.change.player.PlayerLogoffRepository
import com.briarcraft.rtw.change.tile.TileEntityOriginListener
import com.briarcraft.rtw.change.tile.TileEntityOriginRepository
import com.briarcraft.rtw.command.CommandService
import com.briarcraft.rtw.perm.AllPermissionService
import com.briarcraft.rtw.perm.PermissionService
import com.briarcraft.rtw.perm.WorldGuardService
import com.briarcraft.rtw.restore.ProgressiveRestorer
import com.briarcraft.rtw.restore.StructureRestorer
import com.briarcraft.rtw.util.AtomicToggle
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.espi.protectionstones.ProtectionStones
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.event.HandlerList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Suppress("unused")
class ReturnToWildPlugin: SuspendingJavaPlugin() {
//    private lateinit var blockChangeRepo: BlockChangeRepository
    private lateinit var blockChangeRepo: BlockChangeRepository2
    private lateinit var commandService: CommandService
    private lateinit var permService: PermissionService

    override suspend fun onLoadAsync() {
        saveDefaultConfig()

        permService = try {
            WorldGuardService().also { it.load() }
        } catch (e: NoClassDefFoundError) {
            logger.warning("Using default all permissions")
            AllPermissionService().also { it.load() }
        }
    }

    override suspend fun onEnableAsync() {
        val plugin = this

        val pauseFlag = AtomicToggle()

        val dataSource = server.servicesManager.getRegistration(DataSourceService::class.java)?.provider
        require(dataSource != null)

        permService.enable()

//        blockChangeRepo = BlockChangeRepository(server, dataSource).also { it.createTable() }
        val maxFileCacheCount = 500
        val checkDelay = 10.seconds
        val staleDelay = 60.seconds
        val syncDelay = 60.seconds
        val doLog = true
        blockChangeRepo = BlockChangeRepository2(server, dataSource, plugin.dataFolder, maxFileCacheCount, checkDelay, staleDelay, syncDelay, doLog).also { it.createTable() }
        val entityOriginRepo = EntityOriginRepository(server, dataSource).also { it.createTable() }
        val tileEntityOriginRepo = TileEntityOriginRepository(server, dataSource).also { it.createTable() }
        val playerLogoffRepo = PlayerLogoffRepository(server, dataSource).also { it.createTable() }

        server.pluginManager.registerSuspendingEvents(BlockChangeListener(permService, blockChangeRepo, BlockChangeConfig()), plugin)
        server.pluginManager.registerSuspendingEvents(EntityOriginListener(plugin, entityOriginRepo), plugin)
        server.pluginManager.registerSuspendingEvents(TileEntityOriginListener(tileEntityOriginRepo), plugin)
        server.pluginManager.registerSuspendingEvents(PlayerListener(plugin, playerLogoffRepo), plugin)
        server.pluginManager.registerSuspendingEvents(StructureRestorer(this, entityOriginRepo), plugin)

        try {
            ProtectionStones.getInstance()
            server.pluginManager.registerSuspendingEvents(ClaimChangeListener(logger, blockChangeRepo), plugin)
        } catch (e: NoClassDefFoundError) {
            logger.warning("ProtectionStones plugin not available, will not listen for claim changes")
        }

        commandService = CommandService(plugin, permService, blockChangeRepo, entityOriginRepo, pauseFlag)
        commandService.registerCommands()

        blockChangeRepo.start()
//        sendChangesToDatabaseAsync(blockChangeRepo, 1.seconds)
        ProgressiveRestorer(this, blockChangeRepo, permService, pauseFlag).start()
    }

    override suspend fun onDisableAsync() {
        commandService.unregisterCommands()

        HandlerList.unregisterAll(this)

        blockChangeRepo.stop()
//        // Save all queued actions to the database
//        blockChangeRepo.executeAll(logger)
    }

//    private fun sendChangesToDatabaseAsync(blockChangeRepo: BlockChangeRepository2, wait: Duration) {
//        launch {
//            withContext(asyncDispatcher) {
//                while (true) {
//                    // Save actions to the database, in order
//                    delay(wait)
//                    blockChangeRepo.executeNext(logger, 500)
//                }
//            }
//        }
//    }
}
