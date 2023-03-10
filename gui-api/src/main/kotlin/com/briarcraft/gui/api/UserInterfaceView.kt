package com.briarcraft.gui.api

import net.kyori.adventure.text.Component
import org.bukkit.inventory.InventoryView

/**
 * A UserInterfaceView is a Player-specific instance of a UserInterface. It contains all the UserInterfacePanels
 * associated with this view. The top-panel is where the opened Inventory is, a chest, crafting table, etc. and has
 * dynamic slot size. The mid-panel is traditionally where the PlayerInventory is but in a UserInterface is independent
 * of the Player. It has a set number of slots. The nav-panel is the bottom Inventory, called the Quick Bar, and has a
 * set number of slots independent of the Player as well.
 *
 * The lifecycle of a view is:
 * - Create
 * - Open
 * - Update
 * - Click actions with optional Updates
 * - Close
 */
abstract class UserInterfaceView: InventoryView() {

    /**
     * Sets a new title for this UserInterfaceView to display to the Player. Do not call this method from action
     * handlers. Should only be called in the Update event of the lifecycle.
     */
    abstract fun title(title: Component)

    /**
     * Get the UserInterfaceViewHandler globally configured action handlers for this UserInterface.
     * @return The UserInterface global action handlers.
     */
    abstract fun getHandler(): UserInterfaceViewHandler

    /**
     * The top Panel. The nameable Inventory contents opened in the View.
     */
    abstract val topPanel: UserInterfacePanel

    /**
     * The middle Panel, outside the UserInterface the PlayerInventory. Independent of Player.
     */
    abstract val midPanel: UserInterfacePanel

    /**
     * The nav Panel, outside the UserInterface known as the Quick Bar. Independent of Player.
     */
    abstract val navPanel: UserInterfacePanel
}
