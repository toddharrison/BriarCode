package com.briarcraft.rtw

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.change.block.BlockChangeConfig
import com.briarcraft.rtw.change.block.BlockChangeListener
import com.briarcraft.rtw.change.block.BlockChangeRepository2
import com.briarcraft.rtw.change.claim.ClaimChangeListener
import com.briarcraft.rtw.change.entity.EntityOriginListener
import com.briarcraft.rtw.change.entity.EntityOriginRepository
import com.briarcraft.rtw.change.player.PlayerListener
import com.briarcraft.rtw.change.player.PlayerLogoffRepository
import com.briarcraft.rtw.change.tile.TileEntityOriginListener
import com.briarcraft.rtw.change.tile.TileEntityOriginRepository
import com.briarcraft.rtw.command.CommandService
import com.briarcraft.rtw.config.loadDataSynchronizationConfig
import com.briarcraft.rtw.config.loadRestorerConfig
import com.briarcraft.rtw.perm.AllPermissionService
import com.briarcraft.rtw.perm.PermissionService
import com.briarcraft.rtw.perm.WorldGuardService
import com.briarcraft.rtw.restore.ProgressiveRestorer
import com.briarcraft.rtw.restore.StructureRestorer
import com.briarcraft.rtw.util.AtomicToggle
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.espi.protectionstones.ProtectionStones
import org.bukkit.event.HandlerList

@Suppress("unused")
class ReturnToWildPlugin: SuspendingJavaPlugin() {
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

        val dataSynchronizationConfig = loadDataSynchronizationConfig(config)
        blockChangeRepo = BlockChangeRepository2(server, dataSource, plugin.dataFolder, dataSynchronizationConfig).also { it.createTable() }
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

        val restorerConfig = loadRestorerConfig(config)
        ProgressiveRestorer(this, blockChangeRepo, permService, pauseFlag, restorerConfig).start()
    }

    override suspend fun onDisableAsync() {
        commandService.unregisterCommands()

        HandlerList.unregisterAll(this)

        blockChangeRepo.stop()
    }
}
