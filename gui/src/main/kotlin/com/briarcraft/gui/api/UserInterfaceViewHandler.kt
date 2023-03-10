package com.briarcraft.gui.api

/**
 * A UserInterfaceViewHandler represents the UserInterface configuration for all Views generated by it. This includes
 * handlers for all the lifecycle events of a UserInterfaceView and for click and update calls as well. Action handlers
 * defined in the global UserInterfaceViewHandler can be overridden by Panel-specific actions for buttons. Use the click
 * action handlers in this class for global behaviors not tied to specific buttons in the Panels, but to general dynamic
 * Inventory handling in the Panels (i.e. for a shopping cart of ItemStacks that are not managed as buttons).
 */
interface UserInterfaceViewHandler {
    /**
     * Step 1 of the UserInterfaceView lifecycle. Called when the View is first created.
     */
    val onCreate: ViewHandler

    /**
     * Step 2 of the UserInterfaceView lifecycle. Called right before it is opened for the Player.
     */
    val onOpen: ViewHandler

    /**
     * Step 3 of the UserInterfaceView lifecycle. Called right before it is opened for the Player and when actions can
     * change the contents of the View.
     */
    val onUpdate: UpdateViewHandler

    /**
     * Called when the Player clicks outside the UserInterface.
     */
    val onClickOutside: ClickViewHandler

    /**
     * Called when the Player clicks a slot in the top Inventory.
     */
    val onClickTop: ClickViewHandler

    /**
     * Called when the Player clicks a slot in the bottom Inventory, but not the Quick Bar.
     */
    val onClickBottom: ClickViewHandler

    /**
     * Called when the Player clicks a slot in the Quick Bar of the bottom Inventory.
     */
    val onClickQuickBar: ClickViewHandler

    /**
     * Step 4 of the UserInterfaceView lifecycle. Called right before the UserInterfaceView closes.
     */
    val onClose: ViewHandler
}
