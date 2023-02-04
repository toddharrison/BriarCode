package com.briarcraft.gui.util

import com.briarcraft.gui.Plugin
import com.briarcraft.gui.api.UserInterface
import com.briarcraft.kotlin.util.isNoInventoryOpen
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent

fun Player.openUI(ui: UserInterface) =
    if (isNoInventoryOpen(this) || isUIOpen(this)) {
        server.scheduler.runTask(Plugin) { ->
            openInventory(ui.createView(this))
        }
    } else null

fun Player.closeUI() =
    if (isUIOpen(this)) {
        server.scheduler.runTask(Plugin) { ->
            closeInventory(InventoryCloseEvent.Reason.PLUGIN)
        }
    } else null
