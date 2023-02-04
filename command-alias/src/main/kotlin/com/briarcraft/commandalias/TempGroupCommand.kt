package com.briarcraft.commandalias

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import net.luckperms.api.model.data.TemporaryNodeMergeStrategy
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration

class TempGroupCommand {
    private lateinit var adminPerm: String
    private lateinit var adminTimeout: String
    private lateinit var adminTitle: String
    private lateinit var adminColor: BarColor

    private lateinit var creatorPerm: String
    private lateinit var creatorTimeout: String
    private lateinit var creatorTitle: String
    private lateinit var creatorColor: BarColor

    private lateinit var questDevPerm: String
    private lateinit var questDevTimeout: String
    private lateinit var questDevTitle: String
    private lateinit var questDevColor: BarColor

    private lateinit var managerPerm: String
    private lateinit var managerTimeout: String
    private lateinit var managerTitle: String
    private lateinit var managerColor: BarColor

    private lateinit var adminBossBar: BossBar
    private lateinit var creatorBossBar: BossBar
    private lateinit var questDevBossBar: BossBar
    private lateinit var managerBossBar: BossBar

    fun register(plugin: Plugin, luckPerms: LuckPerms) {
        adminBossBar = plugin.server.createBossBar(adminTitle, adminColor, BarStyle.SOLID).also { it.isVisible = true }
        creatorBossBar = plugin.server.createBossBar(creatorTitle, creatorColor, BarStyle.SOLID).also { it.isVisible = true }
        questDevBossBar = plugin.server.createBossBar(questDevTitle, questDevColor, BarStyle.SOLID).also { it.isVisible = true }
        managerBossBar = plugin.server.createBossBar(managerTitle, managerColor, BarStyle.SOLID).also { it.isVisible = true }

        CommandAPICommand("roleadmin")
            .withPermission("commandalias.cmd.lp_temp_admin")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
                    user.data().add(
                        PermissionNode.builder(adminPerm).expiry(Duration.parse("PT$adminTimeout")).build(),
                        TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER)
                    if (!adminBossBar.players.contains(player)) adminBossBar.addPlayer(player)
                }
            }).register()

        CommandAPICommand("rolecreator")
            .withPermission("commandalias.cmd.lp_temp_creator")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
                    user.data().add(
                        PermissionNode.builder(creatorPerm).expiry(Duration.parse("PT$creatorTimeout")).build(),
                        TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER)
                    if (!creatorBossBar.players.contains(player)) {
                        creatorBossBar.addPlayer(player)
                        plugin.launch {
                            player.gameMode = GameMode.CREATIVE
                            player.performCommand("rtw context creator-${player.uniqueId}")
                            player.performCommand("region bypass")
                        }
                    }
                }
            }).register()

        CommandAPICommand("rolequestdev")
            .withPermission("commandalias.cmd.lp_temp_questdev")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
                    user.data().add(
                        PermissionNode.builder(questDevPerm).expiry(Duration.parse("PT$questDevTimeout")).build(),
                        TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER)
                    if (!questDevBossBar.players.contains(player)) questDevBossBar.addPlayer(player)
                }
            }).register()

        CommandAPICommand("rolemanager")
            .withPermission("commandalias.cmd.lp_temp_manager")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
                    user.data().add(
                        PermissionNode.builder(managerPerm).expiry(Duration.parse("PT$managerTimeout")).build(),
                        TemporaryNodeMergeStrategy.REPLACE_EXISTING_IF_DURATION_LONGER)
                    if (!managerBossBar.players.contains(player)) managerBossBar.addPlayer(player)
                }
            }).register()

        CommandAPICommand("roleclear")
            .withPermission("commandalias.cmd.lp_temp_clear")
            .executesPlayer(PlayerCommandExecutor { player, _ ->
                modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
                    user.data().clear(NodeType.PERMISSION.predicate { n -> n.permission.startsWith("plhide.group") })
                    if (adminBossBar.players.contains(player)) adminBossBar.removePlayer(player)
                    if (creatorBossBar.players.contains(player)) {
                        creatorBossBar.removePlayer(player)
                        plugin.launch {
                            player.performCommand("rtw clear creator-${player.uniqueId}")
                            player.performCommand("region bypass")
                        }
                    }
                    if (questDevBossBar.players.contains(player)) questDevBossBar.removePlayer(player)
                    if (managerBossBar.players.contains(player)) managerBossBar.removePlayer(player)
                    player.sendMessage(Component.text("All elevated roles cleared.", NamedTextColor.GREEN))
                }
            }).register()
    }

    fun removeTempPermissions(plugin: Plugin, luckPerms: LuckPerms, player: Player) {
        modifyUserAndUpdateGroups(plugin, luckPerms, player) { user ->
            user.data().clear(NodeType.PERMISSION.predicate { n -> n.permission.startsWith("plhide.group") })
            if (creatorBossBar.players.contains(player)) {
                plugin.launch {
                    player.performCommand("rtw clear creator-${player.uniqueId}")
                }
            }
        }
    }

    fun unregister() {
        CommandAPI.unregister("roleadmin")
        CommandAPI.unregister("rolecreator")
        CommandAPI.unregister("rolequestdev")
        CommandAPI.unregister("rolemanager")
        CommandAPI.unregister("roleclear")

        adminBossBar.also { it.isVisible = false }.removeAll()
        creatorBossBar.also { it.isVisible = false }.removeAll()
        questDevBossBar.also { it.isVisible = false }.removeAll()
        managerBossBar.also { it.isVisible = false }.removeAll()
    }

    fun load(config: Configuration) {
        adminPerm = config.getString("roles.admin.perm") ?: "plhide.group.admin"
        adminTimeout = config.getString("roles.admin.timeout") ?: "1h"
        adminTitle = config.getString("roles.admin.title") ?: "--- Admin ---"
        adminColor = BarColor.valueOf(config.getString("roles.admin.color") ?: "RED")

        creatorPerm = config.getString("roles.creator.perm") ?: "plhide.group.creator"
        creatorTimeout = config.getString("roles.creator.timeout") ?: "4h"
        creatorTitle = config.getString("roles.creator.title") ?: "--- Creator ---"
        creatorColor = BarColor.valueOf(config.getString("roles.creator.color") ?: "GREEN")

        questDevPerm = config.getString("roles.questdev.perm") ?: "plhide.group.questdev"
        questDevTimeout = config.getString("roles.questdev.timeout") ?: "4h"
        questDevTitle = config.getString("roles.questdev.title") ?: "--- Quest Dev ---"
        questDevColor = BarColor.valueOf(config.getString("roles.questdev.color") ?: "BLUE")

        managerPerm = config.getString("roles.manager.perm") ?: "plhide.group.manager"
        managerTimeout = config.getString("roles.manager.timeout") ?: "1h"
        managerTitle = config.getString("roles.manager.title") ?: "--- Manager ---"
        managerColor = BarColor.valueOf(config.getString("roles.manager.color") ?: "YELLOW")
    }

    private fun modifyUserAndUpdateGroups(plugin: Plugin, luckPerms: LuckPerms, player: Player, action: (User) -> Unit) {
        luckPerms.userManager.modifyUser(player.uniqueId) { user ->
            action(user)
        }.whenCompleteAsync { _, error ->
            if (error == null) {
                Bukkit.getServer().scheduler.runTask(plugin) { ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender,
                        "plhide updategroup ${player.name}")
                }
            } else {
                plugin.logger.warning(error.message)
            }
        }
    }
}
