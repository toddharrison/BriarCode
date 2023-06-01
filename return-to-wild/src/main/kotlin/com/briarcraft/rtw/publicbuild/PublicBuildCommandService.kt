package com.briarcraft.rtw.publicbuild

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

class PublicBuildCommandService(private val publicBuildService: PublicBuildService) {
    private val commandRoot = "publicbuild"
    private val commandPrefix = "return_to_wild.cmd.$commandRoot"
    private val adminPrefix = "return_to_wild.cmd.admin.$commandRoot"

    fun registerCommands() {
        CommandAPICommand(commandRoot)
            .withSubcommand(CommandAPICommand("on")
                .withPermission("$commandPrefix.on")
                .withOptionalArguments(PlayerArgument("player").withPermission("$adminPrefix.on"))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val player = args.getOrDefault(0, sender) as Player
                    publicBuildService.toggleOn(player)
                    if (sender == player) {
                        sender.sendMessage(Component.text("Turned ON public build")
                            .color(NamedTextColor.GRAY))
                    } else {
                        sender.sendMessage(Component.text("Turned ON public build for ${player.name}")
                            .color(NamedTextColor.GRAY))
                    }
                }))
            .withSubcommand(CommandAPICommand("off")
                .withPermission("$commandPrefix.off")
                .withOptionalArguments(PlayerArgument("player").withPermission("$adminPrefix.off"))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val player = args.getOrDefault(0, sender) as Player
                    if (publicBuildService.toggleOff(player)) {
                        if (sender == player) {
                            sender.sendMessage(Component.text("Turned OFF public build")
                                .color(NamedTextColor.GRAY))
                        } else {
                            sender.sendMessage(Component.text("Turned OFF public build for ${player.name}")
                                .color(NamedTextColor.GRAY))
                        }
                    } else {
                        if (sender == player) {
                            sender.sendMessage(Component.text("You are not in public build mode")
                                .color(NamedTextColor.GRAY))
                        } else {
                            sender.sendMessage(Component.text("${player.name} was not in public build mode")
                                .color(NamedTextColor.GRAY))
                        }
                    }
                }))
            .withSubcommand(CommandAPICommand("info")
                .withPermission("$commandPrefix.info")
                .withOptionalArguments(PlayerArgument("player").withPermission("$adminPrefix.info"))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val player = args.getOrDefault(0, sender) as Player
                    val curCount = publicBuildService.getCurrentCount(player)
                    val maxAllowance = publicBuildService.getMaxCount(player)
                    if (sender == player) {
                        if (maxAllowance == null) {
                            sender.sendMessage(Component.text("You have no public build allowance")
                                .color(NamedTextColor.GRAY))
                        } else {
                            sender.sendMessage(Component.text("You have used $curCount of $maxAllowance allowance")
                                .color(NamedTextColor.GRAY))
                        }
                    } else {
                        if (maxAllowance == null) {
                            sender.sendMessage(Component.text("${player.name} has no public build allowance")
                                .color(NamedTextColor.GRAY))
                        } else {
                            sender.sendMessage(Component.text("${player.name} has used $curCount of $maxAllowance allowance")
                                .color(NamedTextColor.GRAY))
                        }
                    }
                }))
            .withSubcommand(CommandAPICommand("set")
                .withPermission("$adminPrefix.set")
                .withArguments(PlayerArgument("player"), IntegerArgument("maxAllowance", 0))
                .executes(CommandExecutor { sender, args ->
                    val player = args[0] as Player
                    val maxAllowance = args[1] as Int
                    publicBuildService.setAllowances(player, maxAllowance)
                    sender.sendMessage(Component.text("Set public build allowances for ${player.name} to $maxAllowance")
                        .color(NamedTextColor.GRAY))
                }))
            .withPermission("$commandPrefix.usage")
            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                if (sender.hasPermission("$commandPrefix.usage")) {
                    var message = Component.text("Public Build Usage:")
                    if (sender.hasPermission("$commandPrefix.on")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild on -> toggle on public build mode"))
                    if (sender.hasPermission("$adminPrefix.on")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild on <player> -> toggle on public build mode for the player"))
                    if (sender.hasPermission("$commandPrefix.off")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild off -> toggle off public build mode"))
                    if (sender.hasPermission("$adminPrefix.off")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild off <player> -> toggle off public build mode for the player"))
                    if (sender.hasPermission("$commandPrefix.info")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild info -> get the public build allowance"))
                    if (sender.hasPermission("$adminPrefix.info")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild info <player> -> get the public build allowance for the player"))
                    if (sender.hasPermission("$adminPrefix.set")) message = message.append(Component.newline())
                        .append(Component.text("/publicbuild set <player> <allowance> -> set the public build allowance for the player"))
                    sender.sendMessage(message.color(NamedTextColor.GRAY))
                }
            })
            .register()
    }

    fun unregisterCommands() {
        CommandAPI.unregister(commandRoot)
    }
}
