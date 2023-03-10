package com.briarcraft.gui

import com.briarcraft.gui.api.UserInterfacePanel
import com.briarcraft.gui.api.PanelAction
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class UserInterfacePanelImpl(
    private val inventory: Inventory,
    override val slots: IntRange
): Inventory, UserInterfacePanel {
    private val size: Int = slots.count()
    private val offset = slots.first

    override fun iterator() = slots.map(inventory::getItem).toMutableList().listIterator()
    override fun iterator(slot: Int) = slots.map(inventory::getItem).toMutableList().listIterator(slot)

    override fun getSize() = size

    override fun getMaxStackSize() = inventory.maxStackSize

    override fun setMaxStackSize(size: Int) { inventory.maxStackSize = size }

    override fun getItem(slot: Int) = require(slot in 0 until size).let {
        inventory.getItem(slot + offset)
    }

    override fun setItem(slot: Int, item: ItemStack?) = require(slot in 0 until size).let {
        inventory.setItem(slot + offset, item)
    }

    override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = HashMap<Int, ItemStack>()
        items.forEachIndexed { slot, item ->
            while (true) {
                // Do we already have a stack of it?
                val firstPartial: Int = firstPartial(item)

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    val firstFree = firstEmpty()
                    if (firstFree == -1) {
                        // No space at all!
                        leftover[slot] = item
                        break
                    } else {
                        // More than a single stack!
                        if (item.amount > inventory.maxStackSize) {
                            val stack = item.clone()
                            stack.amount = inventory.maxStackSize
                            setItem(firstFree, stack)
                            item.amount = item.amount - inventory.maxStackSize
                        } else {
                            // Just store it
                            setItem(firstFree, item)
                            break
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    val partialItem = getItem(firstPartial)!!
                    val amount = item.amount
                    val partialAmount = partialItem.amount
                    val maxAmount = partialItem.maxStackSize

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.amount = amount + partialAmount
                        setItem(firstPartial, partialItem)
                        break
                    }

                    // It fits partially
                    partialItem.amount = maxAmount
                    setItem(firstPartial, partialItem)
                    item.amount = amount + partialAmount - maxAmount
                }
            }
        }
        return leftover
    }

    override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
        val leftover = HashMap<Int, ItemStack>()
        items.forEachIndexed { slot, item ->
            var toDelete = item.amount
            while (true) {
                val first: Int = first(item, false)

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.amount = toDelete
                    leftover[slot] = item
                    break
                } else {
                    val itemStack = getItem(first)!!
                    val amount = itemStack.amount
                    if (amount <= toDelete) {
                        toDelete -= amount
                        // clear the slot, all used up
                        this.clear(first)
                    } else {
                        // split the stack and store
                        itemStack.amount = amount - toDelete
                        setItem(first, itemStack)
                        toDelete = 0
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break
                }
            }
        }
        return leftover
    }

    override fun removeItemAnySlot(vararg items: ItemStack) = removeItem(*items)

    override fun getContents(): Array<ItemStack?> {
        return inventory.contents.copyOfRange(slots.first, slots.last)
    }

    override fun setContents(items: Array<out ItemStack?>) {
        require(getSize() >= items.size) { "Invalid inventory size; expected $size or less" }
        clear()
        items.forEachIndexed { slot, item -> setItem(slot, item) }
    }

    override fun contains(material: Material) = slots.map(inventory::getItem).any { it?.type == material }

    override fun contains(item: ItemStack?): Boolean {
        if (item == null) return false
        return slots.map(inventory::getItem).any(item::equals)
    }

    override fun contains(material: Material, amount: Int): Boolean {
        if (amount <= 0) return true
        var amountRemaining = amount
        slots.forEach { slot ->
            inventory.getItem(slot)?.let {
                if (it.type == material) {
                    amountRemaining -= it.amount
                    if (amountRemaining <= 0) return true
                }
            }
        }
        return false
    }

    override fun contains(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true
        return slots.count { inventory.getItem(it) == item } >= amount
    }

    override fun containsAtLeast(item: ItemStack?, amount: Int): Boolean {
        if (item == null) return false
        if (amount <= 0) return true
        var amountRemaining = amount
        slots.forEach { slot ->
            inventory.getItem(slot)?.let {
                if (item.isSimilar(it)) {
                    amountRemaining -= it.amount
                    if (amountRemaining <= 0) return true
                }
            }
        }
        return false
    }

    override fun all(material: Material) = HashMap(slots
        .mapIndexedNotNull { index, slot ->
            inventory.getItem(slot).let { if (it?.type == material) index to it else null }
        }.toMap()
    )

    override fun all(item: ItemStack?)= HashMap(slots
        .mapIndexedNotNull { index, slot ->
            inventory.getItem(slot).let { if (it == item) index to it else null }
        }.toMap()
    )

    override fun first(material: Material) = (slots.firstOrNull { inventory.getItem(it)?.type == material }?.minus(offset)) ?: -1
    override fun first(item: ItemStack) = first(item, true)
    override fun firstEmpty() = (slots.firstOrNull { inventory.getItem(it) == null }?.minus(offset)) ?: -1
    override fun isEmpty() = slots.map(inventory::getItem).all(Objects::isNull)
    override fun remove(material: Material) = slots.filter { inventory.getItem(it)?.type == material }.forEach(inventory::clear)
    override fun remove(item: ItemStack) = slots.filter { inventory.getItem(it) == item }.forEach(inventory::clear)
    override fun clear(slot: Int): Unit = require(slot in 0 until size).let {
        inventory.clear(slot + offset)
        actions.remove(slot + offset)
    }
    override fun clearRelative(slot: Int): Unit = require(slot in slots).let {
        inventory.clear(slot)
        actions.remove(slot)
    }
    override fun clear() {
        slots.forEach(inventory::clear)
        actions.clear()
    }
//    override fun clear() = slots.forEach { slot -> inventory.clear(slot + offset) }
    override fun getViewers(): MutableList<HumanEntity> = inventory.viewers
    override fun getType() = InventoryType.CHEST
    override fun getHolder() = inventory.holder
    override fun getHolder(useSnapshot: Boolean) = inventory.getHolder(useSnapshot)
    override fun getLocation() = inventory.location

    override fun getStorageContents() = throw UnsupportedOperationException()
    override fun setStorageContents(items: Array<out ItemStack?>) = throw UnsupportedOperationException()
    override fun close(): Int = throw UnsupportedOperationException()



    private fun firstPartial(material: Material): Int {
        slots.forEachIndexed { index, slot ->
            val item = inventory.getItem(slot)
            if (item?.type == material && item.amount < item.maxStackSize) {
                return index
            }
        }
        return -1
    }

    private fun firstPartial(item: ItemStack?): Int {
        if (item == null) return -1
        slots.forEachIndexed { index, slot ->
            val cItem = inventory.getItem(slot)
            if (cItem != null && cItem.amount < cItem.maxStackSize && cItem.isSimilar(item)) {
                return index
            }
        }
        return -1
    }

    private fun first(item: ItemStack?, withAmount: Boolean): Int {
        if (item == null) return -1
        return if (withAmount) {
            (slots.firstOrNull { item == inventory.getItem(it) }?.minus(offset)) ?: -1
        } else {
            (slots.firstOrNull { item.isSimilar(inventory.getItem(it)) }?.minus(offset)) ?: -1
        }
    }



    private val actions: MutableMap<Int, PanelAction> = Int2ObjectOpenHashMap()

    override fun executeAction(event: InventoryClickEvent) = actions[event.slot - offset]?.invoke(event)

    override fun addDisabledButton(icon: ItemStack): Boolean {
        return firstEmpty().let { slot ->
            if (slot != -1) {
                setItem(slot, icon)
                actions[slot] = { null }
                true
            } else false
        }
    }

    override fun setDisabledButton(slot: Int, icon: ItemStack) {
        setItem(slot, icon)
        actions[slot] = { null }
    }

    override fun addButton(icon: ItemStack, action: PanelAction): Boolean {
        return firstEmpty().let { slot ->
            if (slot != -1) {
                setItem(slot, icon)
                actions[slot] = action
                true
            } else false
        }
    }

    override fun setButton(slot: Int, icon: ItemStack, action: PanelAction) {
        setItem(slot, icon)
        actions[slot] = action
    }

    override fun removeButton(slot: Int) {
        clear(slot)
        actions.remove(slot)
    }

    override fun getItems() = contents.filterNotNull()

    override fun update(slot: Int, action: (ItemStack?) -> ItemStack?) = require(slot in 0 until size).let {
        inventory.setItem(slot + offset, action(inventory.getItem(slot + offset)))
    }

    override fun updateRelative(slot: Int, action: (ItemStack?) -> ItemStack?) = require(slot in slots).let {
        inventory.setItem(slot, action(inventory.getItem(slot)))
    }
}
