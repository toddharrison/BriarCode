package com.briarcraft.gui.api

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

interface GuiService {
    fun createUserInterface(
        title: Component,
        handler: UserInterfaceViewHandler,
        type: InventoryType = InventoryType.CHEST,
        rows: Int = 3
    ): UserInterface

    fun createUserInterfaceHolder(
        type: InventoryType = InventoryType.CHEST,
        rows: Int = 3
    ): UserInterfaceHolder

    fun createUserInterfacePanel(
        inventory: Inventory,
        slots: IntRange
    ): UserInterfacePanel

    fun createUserInterfaceView(
        topHolder: UserInterfaceHolder,
        bottomInventory: Inventory,
        player: Player,
        title: Component,
        handler: UserInterfaceViewHandler,
        type: InventoryType = InventoryType.CHEST
    ): UserInterfaceView

    fun createUserInterfaceViewHandler(
        onCreate: ViewHandler = {},
        onOpen: ViewHandler = {},
        onUpdate: UpdateViewHandler = { _, _ -> },
        onClickOutside: ClickViewHandler = { _, _ -> null },
        onClickTop: ClickViewHandler = { _, _ -> null },
        onClickBottom: ClickViewHandler = { _, _ -> null },
        onClickQuickBar: ClickViewHandler = { _, _ -> null },
        onClose: ViewHandler = {}
    ): UserInterfaceViewHandler
}
