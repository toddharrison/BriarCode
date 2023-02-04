package com.briarcraft.gui.impl

import com.briarcraft.gui.api.UserInterface
import com.briarcraft.gui.api.UserInterfaceView
import com.briarcraft.gui.api.UserInterfaceViewHandler
import com.briarcraft.kotlin.util.createEmptyPlayerInventory
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class UserInterfaceImpl(
    private val title: Component,
    private val handler: UserInterfaceViewHandler,
    private val type: InventoryType = InventoryType.CHEST,
    private val rows: Int = 3 // Ignored if type != CHEST
): UserInterface {
    override fun createView(player: Player): UserInterfaceView {
        val view = UserInterfaceViewImpl(
            UserInterfaceHolderImpl(type, rows),
            createEmptyPlayerInventory(player),
            player,
            title,
            handler
        )
        view.getHandler().onCreate(view)
        return view
    }
}
