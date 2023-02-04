package com.briarcraft.econ.api.market

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import com.briarcraft.econ.api.currency.Wallet
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.econ.api.price.PricingManager
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.addItemAtomic
import com.briarcraft.kotlin.util.addItemsAtomic
import com.briarcraft.kotlin.util.removeItemAtomic
import com.briarcraft.kotlin.util.removeItemsAtomic
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.min

interface Market {
    val name: String
    val currencyService: CurrencyService
    val currency: Currency
    val pricing: PricingManager
    val stock: StockManager
        get() = pricing.stock

    fun buy(player: Player, item: ItemStack, priceAdjustment: PriceAdjustment? = null) =  pricing
        .getStackPrice(item, priceAdjustment)?.let { price ->
            val wallet = currencyService.getWallet(player)
            val money = currency.createMoney(price)
            if (wallet.remove(money, "Purchase from market")) {
                if (stock.remove(item) != true) {
                    wallet.add(money, "Refund, stock not available")
                    player.sendMessage(Component.text("Stock not available"))
                    false
                } else {
                    if (player.inventory.addItemAtomic(item)) {
                        player.sendMessage(Component.text("You spent ").append(money.display()))
                        true
                    } else {
                        wallet.add(money, "Refund, not enough inventory space")
                        stock.add(item)
                        player.sendMessage(Component.text("You do not have enough inventory space"))
                        false
                    }
                }
            } else false
        }

    fun buy(player: Player, items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(items, priceAdjustment)?.let { price ->
            val wallet = currencyService.getWallet(player)
            val money = currency.createMoney(price)
            if (wallet.remove(money, "Purchase from market")) {
                if (stock.remove(items) != true) {
                    wallet.add(money, "Refund, stock not available")
                    player.sendMessage(Component.text("Stock not available"))
                    false
                } else {
                    if (player.inventory.addItemsAtomic(items)) {
                        player.sendMessage(Component.text("You spent ").append(money.display()))
                        true
                    } else {
                        wallet.add(money, "Refund, not enough inventory space")
                        stock.add(items)
                        player.sendMessage(Component.text("You do not have enough inventory space"))
                        false
                    }
                }
            } else false
        }

    fun buy(item: ItemStack, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(item, priceAdjustment)?.let { price ->
            stock.canRemove(item)?.let { canRemove ->
                if (canRemove) {
                    if (wallet.remove(currency.createMoney(price), "Purchase from market")) {
                        stock.remove(item)!!
                    } else false
                } else false
            }
        }

    fun buy(items: Iterable<ItemStack>, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(items, priceAdjustment)?.let { price ->
            stock.canRemove(items)?.let { canRemove ->
                if (canRemove) {
                    if (wallet.remove(currency.createMoney(price), "Purchase from market")) {
                        stock.remove(items)
                    } else false
                } else false
            }
        }

    fun buyUpTo(item: ItemStack, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = pricing
        .getUnitPrice(item.type, priceAdjustment)?.let { unitPrice ->
            if (unitPrice > 0) {
                wallet.get(currency)
                    ?.let { cashAvailable -> min(unitPrice * item.amount, cashAvailable) }
                    ?.let { maxToSpend ->
                        val maxStockToBuy = (maxToSpend / unitPrice).toInt()
                        stock.getStock(item.type)?.removable?.let { stockAvailable ->
                            val stockToBuy = min(stockAvailable.toInt(), maxStockToBuy)
                            check(stock.remove(ItemStack(item.type, stockToBuy))!!)
                            check(wallet.remove(currency.createMoney(unitPrice * stockToBuy), "Purchase from market"))
                            ItemStack(item.type, item.amount - stockToBuy)
                        }
                    }
            } else {
                stock.getStock(item.type)?.removable?.let { stockAvailable ->
                    val stockToBuy = min(stockAvailable.toInt(), item.amount)
                    check(stock.remove(ItemStack(item.type, stockToBuy))!!)
                    check(wallet.remove(currency.createMoney(unitPrice * stockToBuy), "Purchase from market"))
                    ItemStack(item.type, item.amount - stockToBuy)
                }
            }
        }

    fun sell(player: Player, item: ItemStack, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(item, priceAdjustment)?.let { price ->
            val wallet = currencyService.getWallet(player)
            val money = currency.createMoney(price)
            wallet.add(money, "Sell to market")
            if (stock.add(item) != true) {
                wallet.remove(money, "Correction, market didn't accept")
                player.sendMessage(Component.text("Stock space not available"))
                false
            } else {
                if (player.inventory.removeItemAtomic(item)) {
                    player.sendMessage(Component.text("You received ").append(money.display()))
                    true
                } else {
                    wallet.add(money, "Correction, you did not have the items")
                    stock.add(item)
                    player.sendMessage(Component.text("You do not have the required item"))
                    false
                }
            }
        }

    fun sell(player: Player, items: Iterable<ItemStack>, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(items, priceAdjustment)?.let { price ->
            val wallet = currencyService.getWallet(player)
            val money = currency.createMoney(price)
            wallet.add(money, "Sell to market")
            if (stock.add(items) != true) {
                wallet.remove(money, "Correction, market didn't accept")
                player.sendMessage(Component.text("Stock space not available"))
                false
            } else {
                if (player.inventory.removeItemsAtomic(items)) {
                    player.sendMessage(Component.text("You received ").append(money.display()))
                    true
                } else {
                    wallet.add(money, "Correction, you did not have the items")
                    stock.add(items)
                    player.sendMessage(Component.text("You do not have the required items"))
                    false
                }
            }
        }

    fun sell(item: ItemStack, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(item, priceAdjustment)?.let { price ->
            stock.canAdd(item)?.let { canAdd ->
                if (canAdd) {
                    wallet.add(currency.createMoney(price), "Sell to market")
                    stock.add(item)!!
                } else false
            }
        }

    fun sell(items: Iterable<ItemStack>, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = pricing
        .getStackPrice(items, priceAdjustment)?.let { price ->
            stock.canAdd(items)?.let { canAdd ->
                if (canAdd) {
                    wallet.add(currency.createMoney(price), "Sell to market")
                    stock.add(items)
                } else false
            }
        }

    fun sellUpTo(item: ItemStack, wallet: Wallet, priceAdjustment: PriceAdjustment? = null) = stock.getStock(item.type)?.addable
        ?.let { spaceAvailable ->
            val stockToSell = min(spaceAvailable.toInt(), item.amount)
            pricing.getUnitPrice(item.type, priceAdjustment)?.let { unitPrice ->
                if (unitPrice > 0) wallet.add(currency.createMoney(unitPrice * stockToSell), "Sell to market")
                check(stock.add(ItemStack(item.type, stockToSell))!!)
                ItemStack(item.type, item.amount - stockToSell)
            }
        } ?: pricing.getStackPrice(item, priceAdjustment)?.let { price ->
            wallet.add(currency.createMoney(price), "Sell to market")
            stock.add(item)!!
            ItemStack(item.type, 0)
        }
}
