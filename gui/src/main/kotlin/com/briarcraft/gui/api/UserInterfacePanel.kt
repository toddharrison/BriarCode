package com.briarcraft.gui.api

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A UserInterfacePanel represents one of the three general zones in a UserInterfaceView. The top-panel is where the
 * opened Inventory is, a chest, crafting bench, etc., and is dynamic in slot size. The mid-panel is where the Player
 * inventory generally is displayed, but for a UserInterface this is a new Inventory not associated with the original
 * PlayerInventory. The mid-panel is fixed in slot size. The nav-panel is the quick bar Inventory location at the bottom
 * of the view and is also fixed in slot size and independent of the PlayerInventory.
 */
interface UserInterfacePanel {
    val slots: IntRange

    /**
     * Add the specified ItemStacks to any available slot or slot with the same item in the UserInterfacePanel.
     * @param items The ItemStacks to put into the UserInterfacePanel.
     * @return ItemStack contents not added to the UserInterfacePanel, mapped by index of the request order.
     * @see clear
     */
    fun addItem(vararg items: ItemStack): Map<Int, ItemStack>

    /**
     * Remove the ItemStack contents at the specified slot location in the underlying Inventory.
     * @param slot The slot location to clear.
     * @see clear
     */
    fun clear(slot: Int)

    /**
     * Removes the ItemStacks from the Panel, but does not remove associated Panel action handlers. Only use this method
     * when managing Panel contents as ItemStacks and using the global UserInterfaceView handlers for actions.
     * @see addItem
     * @see clear
     */
    fun clear()

    /**
     * Removes the ItemStack from the Panel, where the slot refers to the underlying slot number. This is useful for
     * using event slot numbers to affect items in Panel displays.
     */
    fun clearRelative(slot: Int)

    /**
     * Determines if the underlying Inventory contains no ItemStacks. This does not also check for registered action
     * handlers.
     */
    fun isEmpty(): Boolean

    /**
     * Attempt to execute a click event handler on the Panel. If handled by the Panel, the event will not propagate up
     * to the global UserInterfaceView handlers.
     * @param event The InventoryClickEvent to attempt to execute.
     * @return Null if there was no Panel-specific event handler.
     */
    fun executeAction(event: InventoryClickEvent): ViewUpdateEvent?

    /**
     * Adds an interactive, clickable, button in the Panel at the first available slot.
     * @param icon The ItemStack-representation of the button.
     * @param action The PanelAction handler to perform when the icon is clicked.
     * @see setDisabledButton
     * @see removeButton
     */
    fun addButton(icon: ItemStack, action: PanelAction): Boolean

    /**
     * Sets an interactive, clickable, button in the Panel at the specified slot.
     * @param slot The Panel-relative slot to place the button.
     * @param icon The ItemStack-representation of the button.
     * @param action The PanelAction handler to perform when the icon is clicked.
     * @see setDisabledButton
     * @see removeButton
     */
    fun setButton(slot: Int, icon: ItemStack, action: PanelAction)

    /**
     * Sets a disabled, un-clickable, button in the Panel at the first available slot. This is important so that the Panel
     * still recognizes that it should handle the event rather than letting the UserInterfaceView global action handlers
     * take care of it.
     * @param slot The Panel-relative slot to place the disabled button.
     * @param icon The ItemStack-representation of the disabled button.
     * @see setButton
     * @see removeButton
     */
    fun addDisabledButton(icon: ItemStack): Boolean

    /**
     * Sets a disabled, un-clickable, button in the Panel at the specified slot. This is important so that the Panel
     * still recognizes that it should handle the event rather than letting the UserInterfaceView global action handlers
     * take care of it.
     * @param slot The Panel-relative slot to place the disabled button.
     * @param icon The ItemStack-representation of the disabled button.
     * @see setButton
     * @see removeButton
     */
    fun setDisabledButton(slot: Int, icon: ItemStack)

    /**
     * Removes a button icon and its associated action handler.
     * @param slot The Panel-relative slot to remove the button.
     * @see setButton
     * @see setDisabledButton
     */
    fun removeButton(slot: Int)

    /**
     * Get the ItemStacks in this Panel.
     */
    fun getItems(): List<ItemStack>

    fun update(slot: Int, action: (ItemStack?) -> ItemStack?)

    fun updateRelative(slot: Int, action: (ItemStack?) -> ItemStack?)

    fun setContents(items: Array<out ItemStack?>)
}
