package com.briarcraft.fakeblock;

import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.briarcraft.fakeblock.service.GroupServiceImpl;
import com.briarcraft.fakeblock.service.WorldEditService;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.Pair;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class FakeBlockCommand implements TabExecutor {
    public static final @Nonnull String NAME = "fakeblock";

    private final @Nonnull WorldEditService worldEditService;
    private final @Nonnull GroupServiceImpl groupService;
    private final @Nonnull PlayerGroupService playerGroupService;

    @Override
    public boolean onCommand(
            final @Nonnull CommandSender sender,
            final @Nonnull Command command,
            final @Nonnull String label,
            final @Nonnull String[] args
    ) {
        val subcommand = getFromArray(args, 0, "");

        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            sender.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }

        val playerName = getFromArray(args, 2);
        if (sender instanceof Player player) {
            return switch (subcommand) {
                case "create" -> createGroup(player, groupName);
                case "delete" -> deleteGroup(player, groupName);
                case "add" -> addToGroup(player, groupName);
                case "remove" -> removeFromGroup(player, groupName);
                case "show" -> showGroup(player, groupName, playerName);
                case "hide" -> hideGroup(player, groupName, playerName);
                default -> badSubcommand(player);
            };
        } else if (sender instanceof ConsoleCommandSender) {
            return switch (subcommand) {
                case "delete" -> deleteGroup(sender, groupName);
                case "show" -> showGroup(sender, groupName, playerName);
                case "hide" -> hideGroup(sender, groupName, playerName);
                default -> badSubcommand(sender);
            };
        } else return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            final @Nonnull CommandSender sender,
            final @Nonnull Command command,
            final @Nonnull String label,
            final @Nonnull String[] args
    ) {
        if (args.length == 1) {
            return List.of("create", "delete", "add", "remove", "show", "hide");
        } else if (args.length == 2) {
            return groupService.getGroupNames().stream().sorted().toList();
        } else if (args.length == 3) {
            return Stream.concat(Bukkit.getOnlinePlayers().stream(), Arrays.stream(Bukkit.getOfflinePlayers()))
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());
        } else return null;
    }

    private boolean createGroup(final @Nonnull Player player, final @Nonnull String groupName) {
        if (groupService.hasGroup(groupName)) {
            player.sendMessage(Component.text("Group does not exist!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val selection = worldEditService.getPlayerSelection(player);
        if (selection != null) {
            val world = selection.getWorld();
            if (world != null) {
                val realWorld = BukkitAdapter.adapt(world);
                val fakeBlocks = StreamSupport.stream(selection.spliterator(), false)
                        .map(vector -> new Pair<>(
                                new BlockPosition(vector.getX(), vector.getY(), vector.getZ()),
                                BukkitAdapter.adapt(world.getBlock(vector))))
                        .filter(pair -> !pair.getSecond().getMaterial().isAir())
                        .map(pair -> new FakeBlock(pair.getFirst(), pair.getSecond()))
                        .collect(Collectors.toSet());
                val created = groupService.create(groupName, realWorld, fakeBlocks);
                if (created) {
                    fakeBlocks.forEach(fakeBlock ->
                            fakeBlock.getPosition().toLocation(realWorld).getBlock().setType(Material.AIR, false));
                    player.sendMessage(Component.text("Created new fakeblock group!", Style.style(NamedTextColor.GRAY)));
                } else {
                    player.sendMessage(Component.text("Group not created!", Style.style(NamedTextColor.GRAY)));
                }
                return created;
            } else {
                player.sendMessage(Component.text("Selection has no world!", Style.style(NamedTextColor.GRAY)));
            }
        } else {
            player.sendMessage(Component.text("No WorldEdit selection!", Style.style(NamedTextColor.GRAY)));
        }
        return false;
    }

    private boolean deleteGroup(final @Nonnull CommandSender sender, final @Nonnull String groupName) {
        val world = groupService.getWorld(groupName);
        if (world != null) {
            val fakeBlocks = groupService.delete(groupName);
            fakeBlocks.forEach(fakeBlock ->
                    fakeBlock.getPosition().toLocation(world).getBlock().setBlockData(fakeBlock.getBlockData()));
            val isDeleted = !fakeBlocks.isEmpty();
            if (isDeleted) {
                sender.sendMessage(Component.text("Group deleted!", Style.style(NamedTextColor.GRAY)));
            } else {
                sender.sendMessage(Component.text("Group not deleted!", Style.style(NamedTextColor.GRAY)));
            }
            return isDeleted;
        } else {
            sender.sendMessage(Component.text("Group does not exist!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
    }

    private boolean addToGroup(final @Nonnull Player player, final @Nonnull String groupName) {
        if (!groupService.hasGroup(groupName)) {
            player.sendMessage(Component.text("Group does not exist!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val selection = worldEditService.getPlayerSelection(player);
        if (selection != null) {
            val world = selection.getWorld();
            if (world != null) {
                val realWorld = BukkitAdapter.adapt(world);
                if (realWorld.equals(groupService.getWorld(groupName))) {
                    val fakeBlocks = StreamSupport.stream(selection.spliterator(), false)
                            .map(vector -> new Pair<>(
                                    new BlockPosition(vector.getX(), vector.getY(), vector.getZ()),
                                    BukkitAdapter.adapt(world.getBlock(vector))))
                            .filter(pair -> !pair.getSecond().getMaterial().isAir())
                            .map(pair -> new FakeBlock(pair.getFirst(), pair.getSecond()))
                            .collect(Collectors.toSet());
                    val created = groupService.addBlocks(groupName, fakeBlocks);
                    if (created) {
                        fakeBlocks.forEach(fakeBlock ->
                                fakeBlock.getPosition().toLocation(realWorld).getBlock().setType(Material.AIR, false));
                        player.sendMessage(Component.text("Blocks added to group!", Style.style(NamedTextColor.GRAY)));
                    } else {
                        player.sendMessage(Component.text("No blocks added to group!", Style.style(NamedTextColor.GRAY)));
                    }
                    return created;
                }
            } else {
                player.sendMessage(Component.text("Selection has no world!", Style.style(NamedTextColor.GRAY)));
            }
        } else {
            player.sendMessage(Component.text("No WorldEdit selection!", Style.style(NamedTextColor.GRAY)));
        }
        return false;
    }

    private boolean removeFromGroup(final @Nonnull Player player, final @Nonnull String groupName) {
        if (!groupService.hasGroup(groupName)) {
            player.sendMessage(Component.text("Group does not exist!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val selection = worldEditService.getPlayerSelection(player);
        if (selection != null) {
            val world = selection.getWorld();
            if (world != null) {
                val realWorld = BukkitAdapter.adapt(world);
                if (realWorld.equals(groupService.getWorld(groupName))) {
                    val blockPositions = StreamSupport.stream(selection.spliterator(), false)
                            .map(vector -> new BlockPosition(vector.getX(), vector.getY(), vector.getZ()))
                            .collect(Collectors.toSet());
                    val removedBlocks = groupService.removeBlocks(groupName, blockPositions);
                    removedBlocks.forEach(fakeBlock ->
                            fakeBlock.getPosition().toLocation(realWorld).getBlock().setBlockData(fakeBlock.getBlockData()));
                    val blocksRemoved = removedBlocks.size() > 0;
                    if (blocksRemoved) {
                        player.sendMessage(Component.text("Blocks removed from group!", Style.style(NamedTextColor.GRAY)));
                    } else {
                        player.sendMessage(Component.text("No blocks removed from group!", Style.style(NamedTextColor.GRAY)));
                    }
                    return blocksRemoved;
                } else {
                    player.sendMessage(Component.text("Group exists in another world!", Style.style(NamedTextColor.GRAY)));
                }
            } else {
                player.sendMessage(Component.text("Selection has no world!", Style.style(NamedTextColor.GRAY)));
            }
        } else {
            player.sendMessage(Component.text("No WorldEdit selection!", Style.style(NamedTextColor.GRAY)));
        }
        return false;
    }

    private boolean showGroup(final @Nonnull CommandSender sender, final @Nonnull String groupName, final @Nullable String playerName) {
        if (playerName == null) {
            sender.sendMessage(Component.text("No player specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val playerId = Bukkit.getPlayerUniqueId(playerName);
        if (playerId != null) {
            val isShown = playerGroupService.showGroup(groupName, playerId);
            if (isShown) {
                sender.sendMessage(Component.text("Group is shown to player!", Style.style(NamedTextColor.GRAY)));
            } else {
                sender.sendMessage(Component.text("Group not shown to player!", Style.style(NamedTextColor.GRAY)));
            }
            return isShown;
        } else {
            sender.sendMessage(Component.text("Specified invalid player!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
    }

    private boolean hideGroup(final @Nonnull CommandSender sender, final @Nonnull String groupName, final @Nullable String playerName) {
        if (playerName == null) {
            sender.sendMessage(Component.text("No player specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val playerId = Bukkit.getPlayerUniqueId(playerName);
        if (playerId != null) {
            val isHidden = playerGroupService.hideGroup(groupName, playerId);
            if (isHidden) {
                sender.sendMessage(Component.text("Group hidden from player!", Style.style(NamedTextColor.GRAY)));
            } else {
                sender.sendMessage(Component.text("Group not hidden from player!", Style.style(NamedTextColor.GRAY)));
            }
            return isHidden;
        } else {
            sender.sendMessage(Component.text("Specified invalid player!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
    }

    private boolean badSubcommand(final @Nonnull CommandSender sender) {
        sender.sendMessage(Component.text("Unrecognized fakeblock command!", Style.style(NamedTextColor.GRAY)));
        return false;
    }

    private <T> @Nullable T getFromArray(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index];
        } else return null;
    }

    private <T> @Nonnull T getFromArray(T[] array, int index, @Nonnull T defaultValue) {
        if (index >= 0 && index < array.length) {
            return Objects.requireNonNullElse(array[index], defaultValue);
        } else return defaultValue;
    }
}
