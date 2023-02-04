package com.briarcraft.econ.market.view

import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.view.MarketView
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.gui.api.PanelUpdateEvent
import com.briarcraft.gui.api.UserInterface
import com.briarcraft.gui.api.UserInterfacePanel
import com.briarcraft.gui.api.ViewUpdateEvent
import com.briarcraft.gui.impl.UserInterfaceImpl
import com.briarcraft.gui.impl.UserInterfaceViewHandlerImpl
import com.briarcraft.gui.util.openUI
import com.briarcraft.kotlin.util.itemStackOf
import com.briarcraft.kotlin.util.removeItemsAtomic
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import kotlin.math.max

class MarketSellView(
    override val title: Component,
    override val market: Market,
    private val currencyService: CurrencyService,
    allowedItems: Set<Material>,
    override val priceAdjustment: PriceAdjustment
): MarketView {
    private val itemSet = allowedItems.intersect(market.stock.getItems())

    override fun open(player: Player) {
        val sellUserInterface: UserInterface = UserInterfaceImpl(title, UserInterfaceViewHandlerImpl(
            onCreate = { view ->
                renderExitButton(view.navPanel, 8, player)
            },
            onUpdate = { view, event ->
                if (event == null) {
                    // Initialize, first view
                    view.midPanel.setContents(player.inventory.contents.sliceArray(view.midPanel.slots).let { items ->
                        items.forEachIndexed { index, item ->
                            if (!itemSet.contains(item?.type)) {
                                items[index] = null
                            }
                        }
                        items
                    })
                } else {
                    // Update
                    renderSellButtons(view.topPanel, view.navPanel, view.midPanel, player, 6, 7)
                }
            },
            onClickTop = { view, event ->
                removeItemToSell(event, view.midPanel)
            },
            onClickBottom = { view, event ->
                addItemToSell(event, view.topPanel)
            },
            onClickOutside = { _, _ ->
                close(player)
            }
        ), InventoryType.CHEST, 3)

        player.openUI(sellUserInterface)
    }

    // Common
    private fun renderExitButton(panel: UserInterfacePanel, slot: Int, player: Player) {
        panel.setButton(slot, itemStackOf(Material.OAK_DOOR, Component.text("Exit"))) {
            close(player)
        }
    }

    // Different

    private fun renderSellButtons(sellPanel: UserInterfacePanel, navPanel: UserInterfacePanel, playerPanel: UserInterfacePanel, player: Player, acceptSlot: Int, clearSlot: Int) {
        if (sellPanel.isEmpty()) {
            navPanel.setDisabledButton(acceptSlot, itemStackOf(Material.LIGHT_GRAY_CONCRETE, Component.text("Accept")))
            navPanel.setDisabledButton(clearSlot, itemStackOf(Material.LIGHT_GRAY_CONCRETE, Component.text("Clear")))
        } else {
            val estimatedCost = market.pricing.getStackPrice(sellPanel.getItems(), priceAdjustment)

            if (estimatedCost == null) {
                navPanel.setDisabledButton(acceptSlot, itemStackOf(Material.LIGHT_GRAY_CONCRETE, Component.text("Accept"), listOf(Component.text("Some items not available!"))))
            } else {
                val itemPrice = market.pricing.getStackPrice(sellPanel.getItems(), PriceAdjustment(priceAdjustment.priceMultiplier)) ?: 0.0
                navPanel.setButton(acceptSlot, itemStackOf(Material.LIME_CONCRETE, Component.text("Accept"), listOf(
                    Component.text("Item Sell Price ").append(market.currency.display(itemPrice, DecimalFormat("#,##0.#####"))),
                    Component.text(" - Fees (${priceAdjustment.feePercentage}% or ")
                        .append(market.currency.display(priceAdjustment.feeMinimum))
                        .append(Component.text(", whichever is higher)")),
                    Component.text("Estimated total sell price: ")
                        .append(market.currency.display(max(0.0, estimatedCost))),
                    Component.text("(Estimated price may not be price you receive)")))
                ) {
                    val itemsToSell = sellPanel.getItems()
                    if (sellCheckout(player, itemsToSell)) {
                        check(market.stock.add(itemsToSell) == true)
                        close(player)
                    } else ViewUpdateEvent(nav = PanelUpdateEvent())
                }
            }

            navPanel.setButton(clearSlot, itemStackOf(Material.RED_CONCRETE, Component.text("Clear"))) {
                sellPanel.getItems().forEach { playerPanel.addItem(it) }
                sellPanel.clear()
                ViewUpdateEvent(nav = PanelUpdateEvent())
            }
        }
    }

    private fun addItemToSell(event: InventoryClickEvent, sellPanel: UserInterfacePanel): ViewUpdateEvent? {
        return event.currentItem?.let {
            when (event.click) {
                ClickType.LEFT -> 1
                ClickType.SHIFT_LEFT -> Integer.min(Integer.max(it.type.maxStackSize / 8, 1), it.amount)
                ClickType.SHIFT_RIGHT -> Integer.min(Integer.max(it.type.maxStackSize / 2, 1), it.amount)
                ClickType.RIGHT -> it.amount
                else -> null
            }?.let { requestedAmount ->
                if (itemSet.contains(it.type)) {
                    check(sellPanel.addItem(itemStackOf(it.type, amount = requestedAmount)).isEmpty())
                    it.subtract(requestedAmount)
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                } else null
            }
        }
    }

    private fun removeItemToSell(event: InventoryClickEvent, playerPanel: UserInterfacePanel): ViewUpdateEvent? {
        return event.currentItem?.let {
            when (event.click) {
                ClickType.LEFT -> 1
                ClickType.SHIFT_LEFT -> Integer.min(Integer.max(it.type.maxStackSize / 8, 1), it.amount)
                ClickType.SHIFT_RIGHT -> Integer.min(Integer.max(it.type.maxStackSize / 2, 1), it.amount)
                ClickType.RIGHT -> it.amount
                else -> null
            }?.let { requestedAmount ->
                if (itemSet.contains(it.type)) {
                    check(playerPanel.addItem(itemStackOf(it.type, amount = requestedAmount)).isEmpty())
                    it.subtract(requestedAmount)
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                } else null
            }
        }
    }

    private fun sellCheckout(player: Player, items: Iterable<ItemStack>): Boolean {
        market.pricing.getStackPrice(items, priceAdjustment)?.let { price ->
            val money = market.currency.createMoney(max(0.0, price))
            val wallet = currencyService.getWallet(player)
            if (player.inventory.removeItemsAtomic(items)) {
                wallet.add(money, "Sell to market")
                player.sendMessage(Component.text("You received ").append(money.display()))
                return true
            } else {
                player.sendMessage(Component.text("You do not have all the items"))
            }
        }
        return false
    }
}
