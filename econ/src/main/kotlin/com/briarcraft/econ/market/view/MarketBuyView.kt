package com.briarcraft.econ.market.view

import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.view.MarketView
import com.briarcraft.econ.api.market.view.MarketViewGroup
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.gui.api.*
import com.briarcraft.kotlin.util.itemStackOf
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.text.DecimalFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MarketBuyView(
    override val plugin: Plugin,
    private val guiService: GuiService,
    override val title: Component,
    override val market: Market,
    private val currencyService: CurrencyService,
    val groups: Map<String, MarketViewGroup>,
    private val rootGroup: MarketViewGroup,
    override val priceAdjustment: PriceAdjustment
): MarketView {
    override fun open(player: Player) {
        val groupStack = LinkedList<MarketViewGroup>()
        groupStack.push(rootGroup)

        val buyUserInterface: UserInterface = guiService.createUserInterface(title, guiService.createUserInterfaceViewHandler(
            onCreate = { view ->
                renderExitButton(view.navPanel, 8, player)
            },
            onUpdate = { view, event ->
                if (event == null) {
                    // Initialize, first view
                    renderGroupContents(view.topPanel, rootGroup) {
                        groupStack.push(it)
                    }
                    renderCartButtons(view.midPanel, view.navPanel, player, 6, 7)
                } else {
                    // Update
                    val top = event.top
                    if (top != null) {
                        renderBackButton(view.navPanel, 5, groupStack) {
                            groupStack.pop()
                        }
                        renderGroupContents(view.topPanel, groupStack.peek(), top.slots) {
                            groupStack.push(it)
                        }
                    }
                    if (event.nav != null) {
                        renderCartButtons(view.midPanel, view.navPanel, player, 6, 7)
                    }
                }
            },
            onClickTop = { view, event ->
                addItemToCart(event, view.midPanel)
            },
            onClickBottom = { view, event ->
                removeItemFromCart(event, view.midPanel)
            },
            onClickOutside = { _, _ ->
                close(player)
            }
        ), InventoryType.CHEST, 6)

        player.openUI(plugin, buyUserInterface)
    }

    private fun renderGroupContents(panel: UserInterfacePanel, group: MarketViewGroup, slots: Set<Int>? = null, onClick: (MarketViewGroup) -> Unit) {
        if (slots == null) {
            panel.clear()
            group.subGroups?.forEach { subGroupName ->
                val subGroup = groups[subGroupName]
                if (subGroup != null) {
                    panel.addButton(itemStackOf(subGroup.icon, Component.text(subGroup.name))) {
                        onClick(subGroup)
                        ViewUpdateEvent(top = PanelUpdateEvent())
                    }
                }
            }
            group.items?.forEach { type ->
                val available = market.stock.getStock(type)?.removable ?: 0
                val price = market.pricing.getUnitPrice(type, PriceAdjustment(priceAdjustment.priceMultiplier)) ?: 0.0
                panel.addItem(
                    itemStackOf(
                        type, lore = listOf(
                            Component.text("$available in stock"),
                            // TODO Config custom format
                            market.currency.display(price, DecimalFormat("#,##0.#####")).append(Component.text(" per unit")),
                            Component.text(" + Fees (${priceAdjustment.feePercentage}% or ")
                                .append(market.currency.display(priceAdjustment.feeMinimum))
                                .append(Component.text(", whichever is higher)"))
                        )
                    )
                )
            }
        } else {
            check(false) { "Not Implemented" }
        }
    }

    private fun renderExitButton(panel: UserInterfacePanel, slot: Int, player: Player) {
        panel.setButton(slot, itemStackOf(Material.OAK_DOOR, Component.text("Exit"))) {
            close(player)
        }
    }

    private fun renderBackButton(panel: UserInterfacePanel, slot: Int, groupStack: LinkedList<MarketViewGroup>, onClick: (MarketViewGroup) -> Unit) {
        if (groupStack.size > 1) {
            panel.setButton(slot, itemStackOf(Material.ARROW, Component.text("Back"))) {
                val parent = groupStack[1]
                onClick(parent)
                ViewUpdateEvent(top = PanelUpdateEvent())
            }
        } else {
            panel.removeButton(slot)
        }
    }

    private fun renderCartButtons(cartPanel: UserInterfacePanel, navPanel: UserInterfacePanel, player: Player, acceptSlot: Int, clearSlot: Int) {
        if (cartPanel.isEmpty()) {
            navPanel.setDisabledButton(acceptSlot, itemStackOf(Material.LIGHT_GRAY_CONCRETE, Component.text("Accept")))
            navPanel.setDisabledButton(clearSlot, itemStackOf(Material.LIGHT_GRAY_CONCRETE, Component.text("Clear")))
        } else {
            val items = cartPanel.getItems()
            if (market.stock.canRemove(items) == true) {
                val estimatedCost = market.pricing.getStackPrice(items, priceAdjustment)
                if (estimatedCost == null) {
                    navPanel.setDisabledButton(acceptSlot,
                        itemStackOf(
                            Material.LIGHT_GRAY_CONCRETE,
                            Component.text("Accept"),
                            listOf(Component.text("Some items not available!"))
                        )
                    )
                } else if ((currencyService.getWallet(player).get(market.currency) ?: 0.0) < estimatedCost) {
                    navPanel.setDisabledButton(acceptSlot,
                        itemStackOf(
                            Material.LIGHT_GRAY_CONCRETE,
                            Component.text("Accept"),
                            listOf(Component.text("You do not have enough available currency!"))
                        )
                    )
                } else {
                    val itemPrice = market.pricing.getStackPrice(items, PriceAdjustment(priceAdjustment.priceMultiplier)) ?: 0.0
                    navPanel.setButton(acceptSlot,
                        itemStackOf(
                            Material.LIME_CONCRETE,
                            Component.text("Accept"),
                            listOf(
                                Component.text("Item Cost ").append(market.currency.display(itemPrice)),
                                Component.text(" + Fees (${priceAdjustment.feePercentage}% or ")
                                    .append(market.currency.display(priceAdjustment.feeMinimum))
                                    .append(Component.text(", whichever is higher)")),
                                Component.text("Estimated total cost: ").append(market.currency.display(estimatedCost)),
                                Component.text("(Estimated cost may not be price you pay)")
                            )
                        )
                    ) {
                        if (checkout(player, items)) {
                            close(player)
                        } else ViewUpdateEvent(nav = PanelUpdateEvent())
                    }
                }
            } else {
                navPanel.setDisabledButton(acceptSlot,
                    itemStackOf(
                        Material.LIGHT_GRAY_CONCRETE,
                        Component.text("Accept"),
                        listOf(Component.text("Not enough stock in market!"))
                    )
                )
            }

            navPanel.setButton(clearSlot, itemStackOf(Material.RED_CONCRETE, Component.text("Clear"))) {
                cartPanel.clear()
                ViewUpdateEvent(nav = PanelUpdateEvent())
            }
        }
    }

    private fun addItemToCart(event: InventoryClickEvent, cartPanel: UserInterfacePanel): ViewUpdateEvent? {
        return event.currentItem?.let {
            when (event.click) {
                ClickType.LEFT -> 1
                ClickType.SHIFT_LEFT -> max(it.maxStackSize / 8, 1)
                ClickType.SHIFT_RIGHT -> max(it.maxStackSize / 2, 1)
                ClickType.RIGHT -> it.maxStackSize
                else -> null
            }?.let { requestedAmount ->
                market.stock.getStock(it.type)?.let { availableStock ->
                    cartPanel.addItem(
                        itemStackOf(
                            it.type,
                            amount = min(availableStock.removable?.toInt() ?: 0, requestedAmount)
                        )
                    )
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                }
            }
        }
    }

    private fun removeItemFromCart(event: InventoryClickEvent, cartPanel: UserInterfacePanel): ViewUpdateEvent? {
        return event.currentItem?.let {
            when (event.click) {
                ClickType.LEFT -> {
                    cartPanel.updateRelative(event.slot) {
                        if (it != null && it.amount > 1) it.subtract()
                        else null
                    }
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                }
                ClickType.SHIFT_LEFT -> {
                    cartPanel.updateRelative(event.slot) {
                        if (it != null) {
                            val amountToRemove = Integer.max(it.type.maxStackSize / 8, 1)
                            if (it.amount > amountToRemove) it.subtract(amountToRemove)
                            else null
                        } else null
                    }
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                }
                ClickType.SHIFT_RIGHT -> {
                    cartPanel.updateRelative(event.slot) {
                        if (it != null) {
                            val amountToRemove = Integer.max(it.type.maxStackSize / 2, 1)
                            if (it.amount > amountToRemove) it.subtract(amountToRemove)
                            else null
                        } else null
                    }
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                }
                ClickType.RIGHT -> {
                    cartPanel.clearRelative(event.slot)
                    ViewUpdateEvent(nav = PanelUpdateEvent())
                }
                else -> null
            }
        }
    }

    private fun checkout(player: Player, items: Iterable<ItemStack>) = market.buy(player, items, priceAdjustment) == true
}