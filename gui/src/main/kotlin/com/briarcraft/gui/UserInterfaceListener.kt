package com.briarcraft.gui

import com.briarcraft.gui.api.UserInterfaceHolder
import com.briarcraft.gui.api.UserInterfaceView
import com.briarcraft.gui.api.ViewUpdateEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

@Suppress("unused")
class UserInterfaceListener: Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    fun openUserInterface(event: InventoryOpenEvent) {
        val view = event.view
        if (view is UserInterfaceView) {
            view.getHandler().onOpen(view)
            view.getHandler().onUpdate(view, null)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun handleUserInterfaceClickEvents(event: InventoryClickEvent) {
        val view = event.view
        if (view is UserInterfaceView) {
            event.isCancelled = true

            // Ignore troublesome actions
            if (event.click == ClickType.DROP
                || event.click == ClickType.CONTROL_DROP
                || event.click == ClickType.NUMBER_KEY
                || event.click == ClickType.SWAP_OFFHAND
            ) return

            val updateViewEvent: ViewUpdateEvent? = when {
                event.slotType == InventoryType.SlotType.OUTSIDE ->
                    view.getHandler().onClickOutside(view, event)
                event.slotType == InventoryType.SlotType.QUICKBAR ->
                    view.navPanel.executeAction(event) ?: view.getHandler().onClickQuickBar(view, event)
                event.clickedInventory == event.view.topInventory ->
                    view.topPanel.executeAction(event) ?: view.getHandler().onClickTop(view, event)
                event.clickedInventory == event.view.bottomInventory ->
                    view.midPanel.executeAction(event) ?: view.getHandler().onClickBottom(view, event)
                else -> {
                    Plugin.logger.warning("Unhandled UI click: ${event.whoClicked.name} ${event.click} ${event.action} ${event.slotType} ${event.slot} ${event.rawSlot}")
                    return
                }
            }

            if (updateViewEvent != null) {
                view.getHandler().onUpdate(view, updateViewEvent)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun preventDragAndDropInUserInterface(event: InventoryDragEvent) {
        if (event.inventory.holder is UserInterfaceHolder) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun preventPickingUpItemsWhenInUserInterface(event: PlayerAttemptPickupItemEvent) {
        if (event.player.openInventory.topInventory.holder is UserInterfaceHolder) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    fun closeUserInterface(event: InventoryCloseEvent) {
        val view = event.view
        if (view is UserInterfaceView) {
            view.getHandler().onClose(view)

            // Restore player inventory by sending refresh packet to player
            val player = event.player as Player
            player.server.scheduler.runTask(Plugin) { ->
                player.updateInventory()
            }
        }
    }
}
