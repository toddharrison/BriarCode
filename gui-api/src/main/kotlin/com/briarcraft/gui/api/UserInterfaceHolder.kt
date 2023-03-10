package com.briarcraft.gui.api

import org.bukkit.inventory.InventoryHolder

/**
 * A UserInterfaceHolder wraps the top Inventory in a UserInterfaceView instance. The actual Inventory and the Holder
 * must be created at the same time, since they both require reference to each other.
 */
interface UserInterfaceHolder: InventoryHolder
