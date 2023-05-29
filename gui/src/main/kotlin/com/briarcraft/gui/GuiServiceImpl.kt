package com.briarcraft.gui

import com.briarcraft.gui.api.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

class GuiServiceImpl: GuiService {
    override fun createUserInterface(
        title: Component,
        handler: UserInterfaceViewHandler,
        type: InventoryType,
        rows: Int
    ): UserInterface {
        return UserInterfaceImpl(title, handler, type, rows)
    }

    override fun createUserInterfaceHolder(
        type: InventoryType,
        rows: Int
    ): UserInterfaceHolder {
        return UserInterfaceHolderImpl()
    }

    override fun createUserInterfacePanel(
        inventory: Inventory,
        slots: IntRange
    ): UserInterfacePanel {
        return UserInterfacePanelImpl(inventory, slots)
    }

    override fun createUserInterfaceView(
        topHolder: UserInterfaceHolder,
        bottomInventory: Inventory,
        player: Player,
        title: Component,
        handler: UserInterfaceViewHandler,
        type: InventoryType
    ): UserInterfaceView {
        return UserInterfaceViewImpl(topHolder, bottomInventory, player, title, handler, type)
    }

    override fun createUserInterfaceViewHandler(
        onCreate: ViewHandler,
        onOpen: ViewHandler,
        onUpdate: UpdateViewHandler,
        onClickOutside: ClickViewHandler,
        onClickTop: ClickViewHandler,
        onClickBottom: ClickViewHandler,
        onClickQuickBar: ClickViewHandler,
        onClose: ViewHandler
    ): UserInterfaceViewHandler {
        return UserInterfaceViewHandlerImpl(onCreate, onOpen, onUpdate, onClickOutside, onClickTop, onClickBottom, onClickQuickBar, onClose)
    }
}
