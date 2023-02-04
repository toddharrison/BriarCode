package com.briarcraft.gui.impl

import com.briarcraft.gui.api.UserInterfaceHolder
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

class UserInterfaceHolderImpl(
    type: InventoryType = InventoryType.CHEST,
    rows: Int = 3
): UserInterfaceHolder {
    private val inventory: Inventory = if (type != InventoryType.CHEST) {
        Bukkit.createInventory(this, type)
    } else {
        require(rows in 1..6)
        Bukkit.createInventory(this, rows * 9)
    }

    override fun getInventory() = inventory
}
