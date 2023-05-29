package com.briarcraft.gui

import com.briarcraft.gui.api.GuiService
import org.bukkit.event.HandlerList
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

val Plugin: JavaPlugin
    get() = pluginInstance
private lateinit var pluginInstance: GuiPlugin

@Suppress("unused")
class GuiPlugin : JavaPlugin() {
    init { pluginInstance = this }

    override fun onLoad() {
        saveDefaultConfig()
    }

    override fun onEnable() {
        val guiService = GuiServiceImpl()
        server.servicesManager.register(GuiService::class.java, guiService, Plugin, ServicePriority.Normal)
        logger.info("Registered GUI Service")

        server.pluginManager.registerEvents(UserInterfaceListener(), Plugin)
        logger.info("Listening for GUI events")
    }

    override fun onDisable() {
        HandlerList.unregisterAll(Plugin)
        logger.info("Stopped listening for GUI events")

        server.servicesManager.unregisterAll(Plugin)
        logger.info("Unregistered all services")
    }
}
