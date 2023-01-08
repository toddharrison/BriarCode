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
    private static final @Nonnull String COMMAND_CREATE = "create";
    private static final @Nonnull String COMMAND_DELETE = "delete";
    private static final @Nonnull String COMMAND_ADD = "add";
    private static final @Nonnull String COMMAND_REMOVE = "remove";
    private static final @Nonnull String COMMAND_SHOW = "show";
    private static final @Nonnull String COMMAND_HIDE = "hide";
    private static final @Nonnull String COMMAND_SETDEFAULT = "setdefault";

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
        if (sender instanceof Player player) {
            return switch (subcommand) {
                case COMMAND_CREATE -> createGroup(player, args);
                case COMMAND_DELETE -> deleteGroup(player, args);
                case COMMAND_ADD -> addToGroup(player, args);
                case COMMAND_REMOVE -> removeFromGroup(player, args);
                case COMMAND_SHOW -> showGroup(player, args);
                case COMMAND_HIDE -> hideGroup(player, args);
                case COMMAND_SETDEFAULT -> clearAllGroups(player, args);
                default -> badSubcommand(player);
            };
        } else if (sender instanceof ConsoleCommandSender) {
            return switch (subcommand) {
                case COMMAND_DELETE -> deleteGroup(sender, args);
                case COMMAND_SHOW -> showGroup(sender, args);
                case COMMAND_HIDE -> hideGroup(sender, args);
                case COMMAND_SETDEFAULT -> clearAllGroups(sender, args);
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
            return getSubcommandMatches(args[0]);
        } else if (args.length == 2) {
            return switch (args[0]) {
                case COMMAND_CREATE, COMMAND_DELETE, COMMAND_ADD, COMMAND_REMOVE, COMMAND_SHOW, COMMAND_HIDE -> getGroupNameMatches(args[1]);
                case COMMAND_SETDEFAULT -> getPlayerNameMatches(args[1]);
                default -> null;
            };
        } else if (args.length == 3) {
            return switch (args[0]) {
                case COMMAND_CREATE -> getBooleanMatch(args[2]);
                case COMMAND_SHOW, COMMAND_HIDE -> getPlayerNameMatches(args[2]);
                default -> null;
            };
        } else return null;
    }

    private @Nonnull List<String> getSubcommandMatches(final @Nonnull String prefix) {
        return Stream.of(COMMAND_CREATE, COMMAND_DELETE, COMMAND_ADD, COMMAND_REMOVE, COMMAND_SHOW, COMMAND_HIDE, COMMAND_SETDEFAULT)
                .filter(it -> it.startsWith(prefix))
                .toList();
    }

    private @Nonnull List<String> getGroupNameMatches(final @Nonnull String prefix) {
        return groupService.getGroupNames().stream()
                .filter(it -> it.startsWith(prefix))
                .sorted()
                .toList();
    }

    private @Nonnull List<String> getPlayerNameMatches(final @Nonnull String prefix) {
        return Stream.concat(Bukkit.getOnlinePlayers().stream(), Arrays.stream(Bukkit.getOfflinePlayers()))
                .map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .filter(it -> it.startsWith(prefix))
                .sorted()
                .toList();
    }

    private @Nonnull List<String> getBooleanMatch(final @Nonnull String prefix) {
        return Stream.of("true", "false")
                .filter(it -> it.startsWith(prefix.toLowerCase()))
                .toList();
    }

    private boolean createGroup(final @Nonnull Player player, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            player.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        if (groupService.hasGroup(groupName)) {
            player.sendMessage(Component.text("Group does not exist!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val isGroupShownByDefault = Boolean.parseBoolean(getFromArray(args, 2));
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
                val created = groupService.create(groupName, realWorld, fakeBlocks, isGroupShownByDefault);
                if (created) {
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

    private boolean deleteGroup(final @Nonnull CommandSender sender, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            sender.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val world = groupService.getWorld(groupName);
        if (world != null) {
            val fakeBlocks = groupService.delete(groupName);
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

    private boolean addToGroup(final @Nonnull Player player, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            player.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
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

    private boolean removeFromGroup(final @Nonnull Player player, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            player.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
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

    private boolean showGroup(final @Nonnull CommandSender sender, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            sender.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val playerName = getFromArray(args, 2);
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

    private boolean hideGroup(final @Nonnull CommandSender sender, final @Nonnull String[] args) {
        val groupName = getFromArray(args, 1);
        if (groupName == null) {
            sender.sendMessage(Component.text("No group specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val playerName = getFromArray(args, 2);
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

    private boolean clearAllGroups(final @Nonnull CommandSender sender, final @Nullable String[] args) {
        val playerName = getFromArray(args, 1);
        if (playerName == null) {
            sender.sendMessage(Component.text("No player specified!", Style.style(NamedTextColor.GRAY)));
            return false;
        }
        val playerId = Bukkit.getPlayerUniqueId(playerName);
        if (playerId != null) {
            val cleared = playerGroupService.clearGroups(playerId) != null;
            if (cleared) {
                sender.sendMessage(Component.text("Cleared player groups!", Style.style(NamedTextColor.GRAY)));
            } else {
                sender.sendMessage(Component.text("Player had no groups to clear!", Style.style(NamedTextColor.GRAY)));
            }
            return cleared;
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
