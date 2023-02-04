package com.briarcraft.econ.command

import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.market.MarketService
import com.briarcraft.econ.api.market.view.MarketView
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class MarketCommand(private val marketService: MarketService) {
    private val permissionPrefix = "econ.market"

    private val chatf = NamedTextColor.WHITE
    private val itemf = NamedTextColor.AQUA
    private val quantityf = NamedTextColor.DARK_AQUA
    private val errorf = NamedTextColor.RED

    fun registerCommands() {
        CommandAPICommand("market")
            .withSubcommand(
                CommandAPICommand("open")
                    .withArguments(
                        marketViewArgument(marketService)
                    )
                    .withPermission("$permissionPrefix.open")
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val view = args[0] as MarketView
                        view.open(player)
                    })
            )
            .withSubcommand(
                CommandAPICommand("buy")
                    .withArguments(
                        marketArgument(marketService),
                        marketItemArgument(),
                        IntegerArgument("quantity")
                    )
                    .withPermission("$permissionPrefix.buy")
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val market = args[0] as Market
                        val item = args[1] as Material
                        val quantity = args[2] as Int
                        market.buy(player, ItemStack(item, quantity), null)
                    })
            )
            .withSubcommand(
                CommandAPICommand("sell")
                    .withArguments(
                        marketArgument(marketService),
                        marketItemArgument(),
                        IntegerArgument("quantity")
                    )
                    .withPermission("$permissionPrefix.sell")
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val market = args[0] as Market
                        val item = args[1] as Material
                        val quantity = args[2] as Int
                        market.sell(player, ItemStack(item, quantity), null)
                    })
            )
            .withSubcommand(
                CommandAPICommand("info")
                    .withArguments(
                        marketArgument(marketService),
                        marketItemArgument()
                    )
                    .withPermission("$permissionPrefix.info")
                    .executes(CommandExecutor { sender, args ->
                        val market = args[0] as Market
                        val item = args[1] as Material
                        info(sender, market, item)
                    })
            )
            .withSubcommand(
                CommandAPICommand("stock")
                    .withArguments(
                        marketArgument(marketService),
                        marketItemArgument(),
                        IntegerArgument("quantity")
                    )
                    .withPermission("$permissionPrefix.admin.stock")
                    .executes(CommandExecutor { sender, args ->
                        val market = args[0] as Market
                        val item = args[1] as Material
                        val quantity = args[2] as Int
                        stock(sender, market, item, quantity)
                    })
            )
            .withSubcommand(
                CommandAPICommand("list")
                    .withPermission("$permissionPrefix.admin.list")
                    .executes(CommandExecutor { sender, _ ->
                        list(sender)
                    })
            )
            .register()
    }

    fun unregisterCommands() {
        CommandAPI.unregister("market")
    }



    private fun info(sender: CommandSender, market: Market, item: Material) {
        val price = market.pricing.getUnitPrice(item, null)
        val quantity = market.stock.getStock(item)?.removable ?: 0
        if (price != null) {
            val buyMoney = market.currency.createMoney(price)
            val sellMoney = market.currency.createMoney(price)
            sender.sendMessage(Component.text("$quantity ", quantityf)
                .append(Component.text(item.name, itemf))
                .append(Component.text(" available, sell for ", chatf))
                .append(buyMoney.display())
                .append(Component.text(", buy for ", chatf)
                .append(sellMoney.display())))
        } else sender.sendMessage(Component.text("The ${market.name} market does not deal in ${item.name}", errorf))
    }

    private fun stock(sender: CommandSender, market: Market, item: Material, quantity: Int) {
        market.stock.add(ItemStack(item, quantity))
        sender.sendMessage(Component.text("${item.name} quantity: ${market.stock.getStock(item)?.removable}"))
    }

    private fun list(sender: CommandSender) {
        sender.sendMessage(Component.text("Markets:", chatf))
        marketService.markets.forEach { (marketName, market) ->
            if (market == marketService.defaultMarket) {
                sender.sendMessage(Component.text("   $marketName", chatf, TextDecoration.UNDERLINED))
            } else {
                sender.sendMessage(Component.text("   $marketName"))
            }
        }
    }
}
