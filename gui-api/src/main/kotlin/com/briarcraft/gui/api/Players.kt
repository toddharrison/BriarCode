package com.briarcraft.gui.api

import com.briarcraft.kotlin.util.isNoInventoryOpen
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.Plugin

fun Player.openUI(plugin: Plugin, ui: UserInterface) =
    if (isNoInventoryOpen(this) || isUIOpen(this)) {
        server.scheduler.runTask(plugin) { ->
            openInventory(ui.createView(this))
        }
    } else null

fun Player.closeUI(plugin: Plugin) =
    if (isUIOpen(this)) {
        server.scheduler.runTask(plugin) { ->
            closeInventory(InventoryCloseEvent.Reason.PLUGIN)
        }
    } else null
