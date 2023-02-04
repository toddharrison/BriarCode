package com.briarcraft.gui.api

import org.bukkit.event.inventory.*

/**
 * Action handler for global UserInterface events.
 */
typealias ViewHandler = (UserInterfaceView) -> Unit

/**
 * Update handler for view update events.
 */
typealias UpdateViewHandler = (UserInterfaceView, ViewUpdateEvent?) -> Unit

/**
 * Action handler for global UserInterface click events.
 */
typealias ClickViewHandler = (UserInterfaceView, InventoryClickEvent) -> ViewUpdateEvent?

/**
 * Action handler for UserInterface instance Panel click events.
 */
typealias PanelAction = (InventoryClickEvent) -> ViewUpdateEvent?
