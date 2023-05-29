package com.briarcraft.gui

import com.briarcraft.gui.api.UserInterfaceHolder
import com.briarcraft.gui.api.UserInterfacePanel
import com.briarcraft.gui.api.UserInterfaceView
import com.briarcraft.gui.api.UserInterfaceViewHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import kotlin.UnsupportedOperationException

class UserInterfaceViewImpl(
    private val topHolder: UserInterfaceHolder,
    private val bottomInventory: Inventory,
    private val player: Player,
    private var title: Component,
    private val handler: UserInterfaceViewHandler,
    private val type: InventoryType = InventoryType.CHEST
): UserInterfaceView() {
    override fun getTopInventory() = topHolder.inventory
    override fun getBottomInventory() = bottomInventory
    override fun getPlayer() = player
    override fun getType() = type
    @Deprecated("Deprecated in Java")
    override fun getTitle() = LegacyComponentSerializer.legacySection().serialize(title)
    override fun title() = title

    override fun title(title: Component) {
        this.title = title
        player.openInventory(this)
    }
    override fun setTitle(title: String) {
        throw UnsupportedOperationException("UserInterfaceView requires Component to set title")
    }
    override fun getOriginalTitle(): String {
        throw UnsupportedOperationException("UserInterfaceView does not support an original title String")
    }

    override fun getHandler() = handler
    override val topPanel: UserInterfacePanel = UserInterfacePanelImpl(topHolder.inventory, 0 until topHolder.inventory.size)
    override val midPanel: UserInterfacePanel = UserInterfacePanelImpl(bottomInventory, 9..35)
    override val navPanel: UserInterfacePanel = UserInterfacePanelImpl(bottomInventory, 0..8)
}
