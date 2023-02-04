package com.briarcraft.commandalias

import net.luckperms.api.LuckPerms
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class CommandAliasPlugin: JavaPlugin() {
    private lateinit var tempGroupCommand: TempGroupCommand
    private lateinit var warpCommand: WarpCommand

    override fun onLoad() {
        saveDefaultConfig()

        tempGroupCommand = TempGroupCommand()
        warpCommand = WarpCommand(logger, server)

        logger.info("CommandAlias loaded")
    }

    override fun onEnable() {
        val plugin = this

        val luckPerms = server.servicesManager.getRegistration(LuckPerms::class.java)!!.provider

        tempGroupCommand.load(config)
        tempGroupCommand.register(plugin, luckPerms)
        server.pluginManager.registerEvents(object: Listener {
            @EventHandler(priority = EventPriority.MONITOR)
            fun on(event: PlayerQuitEvent) {
                tempGroupCommand.removeTempPermissions(plugin, luckPerms, event.player)
            }
        }, plugin)

        warpCommand.load(config)
        warpCommand.register()

        logger.info("CommandAlias enabled")
    }

    override fun onDisable() {
        tempGroupCommand.unregister()

        warpCommand.unregister()
        warpCommand.save(config)
        saveConfig()

        logger.info("CommandAlias disabled")
    }
}
