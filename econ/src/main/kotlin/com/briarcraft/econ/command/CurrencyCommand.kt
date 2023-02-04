package com.briarcraft.econ.command

import com.briarcraft.econ.api.currency.Currency
import com.briarcraft.econ.api.currency.CurrencyService
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CurrencyCommand(private val currencyService: CurrencyService) {

    private val chatf = NamedTextColor.WHITE
    private val playerf = NamedTextColor.YELLOW
    private val errorf = NamedTextColor.RED

    private val formatter = DateTimeFormatter
        .ofPattern("yyyy.MM.dd HH:mm")
        .withZone(ZoneId.systemDefault())

    fun registerCommands() {
        CommandAPICommand("pay")
            .withArguments(
                offlinePlayerNotSelfArgument(),
                DoubleArgument("amount")
            )
            .withPermission("econ.currency.pay")
            .executesPlayer(PlayerCommandExecutor { fromPlayer, args ->
                val toPlayer = args[0] as OfflinePlayer
                val amount = args[1] as Double
                val currency = currencyService.defaultCurrency
                pay(fromPlayer, toPlayer, amount, currency)
            }).register()

        CommandAPICommand("pay")
            .withArguments(
                offlinePlayerNotSelfArgument(),
                DoubleArgument("amount"),
                currencyArgument(currencyService)
            )
            .withPermission("econ.currency.pay")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                val toPlayer = args[0] as OfflinePlayer
                val amount = args[1] as Double
                val currency = args[2] as Currency
                pay(sender, toPlayer, amount, currency)
            }).register()

        CommandAPICommand("balance")
            .withAliases("bal")
            .withPermission("econ.currency.balance")
            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                bal(sender)
            })
            .withSubcommand(
                CommandAPICommand("history")
                    .withPermission("econ.currency.balance.history")
                    .executesPlayer(PlayerCommandExecutor { sender, _ ->
                        history(sender)
                    })
            )
            .register()

        CommandAPICommand("balance")
            .withArguments(
                offlinePlayerArgument()
            )
            .withPermission("econ.currency.balance.all")
            .withAliases("bal")
            .executes(CommandExecutor { sender, args ->
                val player = args[0] as OfflinePlayer
                bal(sender, player)
            })
            .withSubcommand(
                CommandAPICommand("history")
                    .withArguments(
                        offlinePlayerArgument()
                    )
                    .withPermission("econ.currency.balance.all.history")
                    .executes(CommandExecutor { sender, args ->
                        val player = args[0] as OfflinePlayer
                        history(sender, player)
                    })
            )
            .register()

        CommandAPICommand("currency")
            .withSubcommand(
                CommandAPICommand("list")
                    .withPermission("econ.currency.list")
                    .executes(CommandExecutor { sender, _ ->
                        list(sender)
                    })
            )
            .withSubcommand(
                CommandAPICommand("give")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount")
                    )
                    .withPermission("econ.currency.admin.give")
                    .executes(CommandExecutor { sender, args ->
                        val toPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = currencyService.defaultCurrency
                        give(sender, toPlayer, amount, currency, false)
                    })
            )
            .withSubcommand(
                CommandAPICommand("give")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        BooleanArgument("silent")
                    )
                    .withPermission("econ.currency.admin.give")
                    .executes(CommandExecutor { sender, args ->
                        val toPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = currencyService.defaultCurrency
                        val silent = args[2] as Boolean
                        give(sender, toPlayer, amount, currency, silent)
                    })
            )
            .withSubcommand(
                CommandAPICommand("give")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        currencyArgument(currencyService)
                    )
                    .withPermission("econ.currency.admin.give")
                    .executes(CommandExecutor { sender, args ->
                        val toPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = args[2] as Currency
                        give(sender, toPlayer, amount, currency, false)
                    })
            )
            .withSubcommand(
                CommandAPICommand("give")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        currencyArgument(currencyService),
                        BooleanArgument("silent")
                    )
                    .withPermission("econ.currency.admin.give")
                    .executes(CommandExecutor { sender, args ->
                        val toPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = args[2] as Currency
                        val silent = args[3] as Boolean
                        give(sender, toPlayer, amount, currency, silent)
                    })
            )
            .withSubcommand(
                CommandAPICommand("take")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount")
                    )
                    .withPermission("econ.currency.admin.take")
                    .executesPlayer(PlayerCommandExecutor { sender, args ->
                        val fromPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = currencyService.defaultCurrency
                        take(sender, fromPlayer, amount, currency, false)
                    })
            )
            .withSubcommand(
                CommandAPICommand("take")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        BooleanArgument("silent")
                    )
                    .withPermission("econ.currency.admin.take")
                    .executesPlayer(PlayerCommandExecutor { sender, args ->
                        val fromPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = currencyService.defaultCurrency
                        val silent = args[2] as Boolean
                        take(sender, fromPlayer, amount, currency, silent)
                    })
            )
            .withSubcommand(
                CommandAPICommand("take")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        currencyArgument(currencyService)
                    )
                    .withPermission("econ.currency.admin.take")
                    .executes(CommandExecutor { sender, args ->
                        val fromPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = args[2] as Currency
                        take(sender, fromPlayer, amount, currency, false)
                    })
            )
            .withSubcommand(
                CommandAPICommand("take")
                    .withArguments(
                        offlinePlayerArgument(),
                        DoubleArgument("amount"),
                        currencyArgument(currencyService),
                        BooleanArgument("silent")
                    )
                    .withPermission("econ.currency.admin.take")
                    .executes(CommandExecutor { sender, args ->
                        val fromPlayer = args[0] as OfflinePlayer
                        val amount = args[1] as Double
                        val currency = args[2] as Currency
                        val silent = args[3] as Boolean
                        take(sender, fromPlayer, amount, currency, silent)
                    })
            )
            .register()
    }

    fun unregisterCommands() {
        CommandAPI.unregister("pay")
        CommandAPI.unregister("balance")
        CommandAPI.unregister("currency")
    }



    private fun list(sender: CommandSender) {
        sender.sendMessage(Component.text("Currencies:", chatf))
        currencyService.currencies.values.forEach { currency ->
            if (currency == currencyService.defaultCurrency) {
                sender.sendMessage(Component.text("   ${currency.name}", chatf, TextDecoration.UNDERLINED))
            } else sender.sendMessage(Component.text("   ${currency.name}", chatf))
        }
    }

    private fun bal(player: Player) {
        val wallet = currencyService.getWallet(player)

        if (wallet.monies.isEmpty() || wallet.monies.all { it.value == 0.0 }) {
            player.sendMessage(Component.text("You have no money", chatf))
        } else {
            player.sendMessage(Component.text("Your balances are:", chatf))
            wallet.monies.forEach { (currency, amount) ->
                player.sendMessage(Component.text("   ")
                    .append(currency.display(amount)))
            }
        }
    }

    private fun bal(sender: CommandSender, player: OfflinePlayer) {
        val wallet = currencyService.getWallet(player)

        if (wallet.monies.isEmpty()) {
            sender.sendMessage(Component.text("${player.name}", playerf)
                .append(Component.text(" has no money", chatf)))
        } else {
            sender.sendMessage(Component.text("Balances for ", chatf)
                .append(Component.text("${player.name}", playerf))
                .append(Component.text(" are:", chatf)))
            wallet.monies.forEach { (currency, amount) ->
                sender.sendMessage(Component.text("   ")
                    .append(currency.display(amount)))
            }
        }
    }

    private fun history(player: Player) {
        val wallet = currencyService.getWallet(player)

        var message = Component.text("Transactions for ${player.name}:").append(Component.newline())
        wallet.history().takeLast(5).forEach { transaction ->
            message = message.append(Component.text(formatter.format(transaction.time)))
                .append(Component.text(" "))
                .append(transaction.money.display())
                .append(Component.text(" "))
                .append(Component.text(transaction.description ?: "Unknown"))
                .append(Component.text(" "))
                .append(transaction.money.currency.display(transaction.balance ?: 0.0))
                .append(Component.newline())
        }
        player.sendMessage(message)
    }

    private fun history(sender: CommandSender, player: OfflinePlayer) {
        val wallet = currencyService.getWallet(player)

        var message = Component.text("Transactions for ${player.name}:").append(Component.newline())
        wallet.history().takeLast(5).forEach { transaction ->
            message = message.append(Component.text(formatter.format(transaction.time)))
                .append(Component.text(" "))
                .append(transaction.money.display())
                .append(Component.text(" "))
                .append(Component.text(transaction.description ?: "Unknown"))
                .append(Component.text(" "))
                .append(transaction.money.currency.display(transaction.balance ?: 0.0))
                .append(Component.newline())
        }
        sender.sendMessage(message)
    }

    private fun pay(fromPlayer: Player, toPlayer: OfflinePlayer, amount: Double, currency: Currency) {
        val fromWallet = currencyService.getWallet(fromPlayer)
        val toWallet = currencyService.getWallet(toPlayer)
        val money = currency.createMoney(amount)

        if (fromWallet != toWallet) {
            if (fromWallet.remove(money, "Payed ${toPlayer.name}")) {
                toWallet.add(money, "Payed by ${fromPlayer.name}")
                fromPlayer.sendMessage(Component.text("Paying ", chatf)
                    .append(money.display())
                    .append(Component.text(" to ", chatf))
                    .append(Component.text("${toPlayer.name}", playerf)))
                toPlayer.player?.sendMessage(Component.text(fromPlayer.name, playerf)
                    .append(Component.text(" paid you ", chatf))
                    .append(money.display()))
            } else fromPlayer.sendMessage(Component.text("Not enough available funds", errorf))
        } else fromPlayer.sendMessage(Component.text("You cannot pay yourself", errorf))
    }

    private fun give(sender: CommandSender, toPlayer: OfflinePlayer, amount: Double, currency: Currency, silent: Boolean) {
        val toWallet = currencyService.getWallet(toPlayer)
        val money = currency.createMoney(amount)

        toWallet.add(money, "Received from server")

        if (!silent) {
            sender.sendMessage(Component.text("Giving ", chatf)
                .append(money.display())
                .append(Component.text(" to ", chatf))
                .append(Component.text("${toPlayer.name}", playerf)))
            toPlayer.player?.sendMessage(Component.text(sender.name, playerf)
                .append(Component.text(" gave you ", chatf))
                .append(money.display()))
        }
    }

    private fun take(sender: CommandSender, fromPlayer: OfflinePlayer, amount: Double, currency: Currency, silent: Boolean) {
        val toWallet = currencyService.getWallet(fromPlayer)
        val money = currency.createMoney(amount)

        val remainder = toWallet.removeUpTo(money, "Taken by server")
        val moneyTaken = if (remainder == null) money else currency.createMoney(money.amount - remainder.amount)

        if (!silent) {
            sender.sendMessage(Component.text("Taking ", chatf)
                .append(moneyTaken.display())
                .append(Component.text(" from ", chatf))
                .append(Component.text("${fromPlayer.name}", playerf)))
            fromPlayer.player?.sendMessage(Component.text(sender.name, playerf)
                .append(Component.text(" took ", chatf))
                .append(moneyTaken.display())
                .append(Component.text(" from you")))
        }
    }
}
