package com.briarcraft.rtw.command

import com.briarcraft.rtw.change.block.BlockChangeRepository
import com.briarcraft.rtw.change.entity.EntityOrigin
import com.briarcraft.rtw.change.entity.EntityOriginRepository
import com.briarcraft.rtw.perm.PermissionService
import com.briarcraft.rtw.util.*
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntityTypeArgument
import dev.jorel.commandapi.arguments.LootTableArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.EntityType
import org.bukkit.loot.LootTable
import org.bukkit.loot.LootTables

class CommandService(
    private val plugin: SuspendingJavaPlugin,
    private val permService: PermissionService,
    private val blockChangeRepo: BlockChangeRepository,
    private val entityOriginRepo: EntityOriginRepository,
    private val pauseFlag: AtomicToggle
) {
    private val commandPrefix = "return_to_wild.cmd"

    fun registerCommands() {
        CommandAPICommand("rtw")
            .withSubcommand(restoreAll())
            .withSubcommand(restoreContext())
            .withSubcommand(clearAll())
            .withSubcommand(clearContext())
            .withSubcommand(clearPlayerContext())
            .withSubcommand(setPlayerContext())
            .withSubcommand(rollbackToOriginalContext())
            .withSubcommand(pauseAutoRestore())
            .withSubcommand(createEntityOrigin())
            .withSubcommand(createEntityOriginWithCustomLootTable())
            .register()
    }

    fun unregisterCommands() {
        CommandAPI.unregister("rtw")
    }

    private fun restoreAll(): CommandAPICommand {
        return CommandAPICommand("restore")
            .withPermission("$commandPrefix.restore")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                plugin.launch {
                    blockChangeRepo.findChanges()
                        .filter { permService.isRestorable(it.location) }
                        .forEach { change ->
                            change.location.block.setBlockData(change.blockData, false)
                            blockChangeRepo.delete(change)
                        }
                    player.sendMessage("Restored")
                }
            })
    }

    private fun restoreContext(): CommandAPICommand {
        return CommandAPICommand("restore")
            .withPermission("$commandPrefix.restore.context")
            .withArguments(
                StringArgument("context")
            )
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val context = args[0] as String
                plugin.launch {
                    blockChangeRepo.findChanges(context)
                        .filter { permService.isRestorable(it.location) }
                        .forEach { change ->
                            change.location.block.setBlockData(change.blockData, false)
                            blockChangeRepo.delete(change)
                        }
                    player.sendMessage("Restored")
                }
            })
    }

    private fun clearAll(): CommandAPICommand {
        return CommandAPICommand("clear")
            .withPermission("$commandPrefix.clear")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                plugin.launch {
                    blockChangeRepo.deleteAll()
                    player.sendMessage("Cleared")
                }
            })
    }

    private fun clearContext(): CommandAPICommand {
        return CommandAPICommand("clear")
            .withPermission("$commandPrefix.clear.context")
            .withArguments(
                StringArgument("context")
            )
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val context = args[0] as String
                plugin.launch {
                    blockChangeRepo.delete(context)
                    player.sendMessage("Cleared")
                }
            })
    }

    private fun clearPlayerContext(): CommandAPICommand {
        return CommandAPICommand("context")
            .withPermission("$commandPrefix.context.clear")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                player.clearContext()
                player.sendMessage("Cleared context")
            })
    }

    private fun setPlayerContext(): CommandAPICommand {
        return CommandAPICommand("context")
            .withPermission("$commandPrefix.context.set")
            .withArguments(
                StringArgument("name")
            )
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val context = args[0] as String
                player.setContext(context)
                player.sendMessage("Set context to '$context'")
            })
    }

    private fun rollbackToOriginalContext(): CommandAPICommand {
        return CommandAPICommand("rollback")
            .withPermission("$commandPrefix.context.rollback")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                val context = player.getContext()
                if (context != CONTEXT_ORIGINAL) {
                    plugin.launch {
                        blockChangeRepo.findChanges(context)
                            .filter { permService.isRestorable(it.location) }
                            .forEach { change ->
                                change.location.block.setBlockData(change.blockData, false)
                                blockChangeRepo.delete(change)
                            }
                        blockChangeRepo.delete(context)
                        player.sendMessage("Rolled-back")
                    }
                }
            })
    }

    private fun pauseAutoRestore(): CommandAPICommand {
        return CommandAPICommand("pause")
            .withPermission("$commandPrefix.pause")
            .executes(CommandExecutor { sender, _ ->
                if (pauseFlag.toggle()) {
                    sender.sendMessage("RTW pausing... check console")
                } else {
                    sender.sendMessage("RTW active")
                }
            })
    }

    private fun createEntityOrigin(): CommandAPICommand {
        return CommandAPICommand("origin")
            .withPermission("$commandPrefix.entity.origin")
            .withArguments(
                EntityTypeArgument("type"),
            )
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val type = args[0] as EntityType
                val loc = player.location
                val loot = LootTables.valueOf(type.name).key
                plugin.launch {
                    entityOriginRepo.save(EntityOrigin(type, loc, player.facing, null, loot, null))
                    player.sendMessage("Created entity origin at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}] for $type")
                }
            })
    }

    private fun createEntityOriginWithCustomLootTable(): CommandAPICommand {
        return CommandAPICommand("origin")
            .withPermission("$commandPrefix.entity.origin")
            .withArguments(
                EntityTypeArgument("type"),
                LootTableArgument("loot"),
            )
            .executesPlayer(PlayerCommandExecutor { player, args ->
                val type = args[0] as EntityType
                val loc = player.location
                val loot = (args[1] as LootTable).key
                plugin.launch {
                    entityOriginRepo.save(EntityOrigin(type, loc, player.facing, null, loot, null))
                    player.sendMessage("Created entity origin at [${loc.blockX}, ${loc.blockY}, ${loc.blockZ}] for $type")
                }
            })
    }
}
