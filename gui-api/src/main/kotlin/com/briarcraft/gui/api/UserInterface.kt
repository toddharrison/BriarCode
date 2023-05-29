package com.briarcraft.gui.api

import org.bukkit.entity.Player

/**
 * A UserInterface represents a UI template that is created and configured ahead of time. An instance, or
 * "UserInterfaceView" is created when a Player first opens the UI and is configured based upon the UserInterface.
 * Action handlers have access to the View instance so that they can change the Panels (Inventories) displayed to the
 * Player to react to an action or other plugin event.
 */
interface UserInterface {
    /**
     * Create a new UserInterfaceView using this UserInterface as the template.
     * @param player The Player to create the view for.
     * @return The new UserInterfaceView.
     */
    fun createView(player: Player): UserInterfaceView
}
