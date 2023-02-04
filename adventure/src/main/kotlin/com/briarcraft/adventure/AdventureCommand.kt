package com.briarcraft.adventure

import com.briarcraft.adventure.api.enchant.updateEnchantingLore
import com.briarcraft.adventure.api.item.CustomItem
import com.briarcraft.adventure.item.CustomItems
import com.destroystokyo.paper.loottable.LootableEntityInventory
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.StorageMinecart
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.loot.LootTable
import org.bukkit.loot.Lootable

class AdventureCommand(customItems: CustomItems) {
    private val adventurePermission = "briar.adventure"

    fun register() {
        CommandAPICommand("adventure")
            .withPermission(adventurePermission)

            .withSubcommand(
                CommandAPICommand("book")
                    .withArguments(
                        enchantmentsArgument(),
                        IntegerArgument("level")
                    )
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val enchant = args[0] as Enchantment
                        val level = args[1] as Int
                        val item = ItemStack(Material.ENCHANTED_BOOK, 1)
                        val meta: EnchantmentStorageMeta = item.itemMeta as EnchantmentStorageMeta
                        val success = meta.addStoredEnchant(enchant, level, false)
                        if (success) {
                            updateEnchantingLore(item)
                            item.itemMeta = meta
                            updateEnchantingLore(item)
                            player.inventory.addItem(item)
                        } else {
                            player.sendMessage("Level $level is invalid for ${enchant.key}")
                        }
                    }))

            .withSubcommand(
                CommandAPICommand("item")
                    .withArguments(
                        customItemArgument()
                    )
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val item = args[0] as CustomItem
                        player.inventory.addItem(item.create())
                    })
            )

            .withSubcommand(
                CommandAPICommand("enchant")
                    .withArguments(
                        EnchantmentArgument("enchant"),
                        IntegerArgument("level")
                    )
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val item = player.inventory.itemInMainHand
                        val enchant = args[0] as Enchantment
                        val level = args[1] as Int
                        if (item.type.isItem && !item.type.isAir) {
                            item.addUnsafeEnchantment(enchant, level)
                        }
                    }))

            .withSubcommand(
                CommandAPICommand("info")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val item = player.inventory.itemInMainHand
                        player.sendMessage(item.toString())
                    })
            )

            .withSubcommand(
                CommandAPICommand("meta")
                    .withArguments(
                        BooleanArgument("unbreakable")
                    )
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val item = player.inventory.itemInMainHand
                        val isUnbreakable = args[0] as Boolean

                        val meta = item.itemMeta
                        meta.isUnbreakable = isUnbreakable
                        item.itemMeta = meta
                    })
            )

            .withSubcommand(
                CommandAPICommand("damage")
                    .withArguments(
                        IntegerArgument("damage")
                    )
                    .executesPlayer(PlayerCommandExecutor { player, args ->
                        val item = player.inventory.itemInMainHand
                        val damage = args[0] as Int
                        val meta = item.itemMeta
                        if (meta is Damageable) {
                            val maxDamage = item.type.maxDurability
                            if (damage <= maxDamage) {
                                meta.damage = damage
                                item.itemMeta = meta
                                player.sendMessage(Component.text("Set ").append(item.displayName()).append(Component.text(" damage to $damage out of $maxDamage")))
                            } else {
                                player.sendMessage(Component.text("Max damage of $maxDamage for ").append(item.displayName()))
                            }
                        }
                    }))

            .withSubcommand(
                CommandAPICommand("loot")
                    .withSubcommand(
                        CommandAPICommand("get")
                            .executesPlayer(PlayerCommandExecutor { player, _ ->
                                val distance = 5
                                val entity = getEntityPlayerIsLookingAt(player, distance, setOf(EntityType.MINECART_CHEST))
                                if (entity is StorageMinecart) {
                                    getEntityLootTable(entity, player)
                                } else {
                                    val block = player.getTargetBlock(null, distance) as Block?
                                    when (val state = block?.state) {
                                        is Chest -> getBlockLootTable(state, player)
                                        is ShulkerBox -> getBlockLootTable(state, player)
                                        is Barrel -> getBlockLootTable(state, player)
                                        else -> player.sendMessage("Not looking at container")
                                    }
                                }
                            }))
                    .withSubcommand(
                        CommandAPICommand("set")
                            .withArguments(
                                LootTableArgument("lootTable")
                            )
                            .executesPlayer(PlayerCommandExecutor { player, args ->
                                val lootTable = args[0] as LootTable?
                                if (lootTable != null) {
                                    val distance = 5
                                    val entity = getEntityPlayerIsLookingAt(player, distance, setOf(EntityType.MINECART_CHEST))
                                    if (entity is StorageMinecart) {
                                        setEntityLootTable(entity, lootTable, player)
                                    } else {
                                        val block = player.getTargetBlock(null, distance) as Block?
                                        when (val state = block?.state) {
                                            is Chest -> setBlockLootTable(state, lootTable, player)
                                            is ShulkerBox -> setBlockLootTable(state, lootTable, player)
                                            is Barrel -> setBlockLootTable(state, lootTable, player)
                                            else -> player.sendMessage("Not looking at container")
                                        }
                                    }
                                }
                            }))
            )
            .register()
    }

    fun unregister() {
        CommandAPI.unregister("adventure")
    }



    @Suppress("SameParameterValue")
    private fun getEntityPlayerIsLookingAt(player: Player, distance: Int, entityTypes: Set<EntityType>): Entity? {
        val eyeLoc = player.eyeLocation
        return player.world.rayTrace(
            eyeLoc,
            player.location.direction,
            distance - 1.0,
            FluidCollisionMode.NEVER,
            true,
            1.0
        ) { e ->
            entityTypes.contains(e.type)
        }?.hitEntity
    }

    private fun getEntityLootTable(entity: LootableEntityInventory, player: Player) {
        val lootTable = entity.lootTable
        player.sendMessage("Loot table is '${lootTable?.key?.asString()}'")
    }

    private fun getBlockLootTable(state: Lootable, player: Player) {
        val lootTable = state.lootTable
        player.sendMessage("Loot table is '${lootTable?.key?.asString()}'")
    }

    private fun <E> setEntityLootTable(entity: E, lootTable: LootTable, player: Player) where E: Entity, E: LootableEntityInventory {
        entity.lootTable = lootTable
        player.sendMessage("Setting loot table '${lootTable.key.asString()}' for ${entity.type}")
    }

    private fun <S> setBlockLootTable(state: S, lootTable: LootTable, player: Player) where S: BlockState, S: Lootable {
        state.lootTable = lootTable
        state.update()
        player.sendMessage("Setting loot table '${lootTable.key.asString()}' for ${state.type}")
    }



    private val allEnchants = Enchantment.values().associateBy { it.key.asString().replace(":", "_") }
    private val allItems = customItems.items.associateBy { it.key.asString().replace(":", "_") }

    private fun enchantmentsArgument(nodeName: String = "enchantment"): Argument<Enchantment> =
        CustomArgument(StringArgument(nodeName)) { nameArg ->
            allEnchants[nameArg.input] ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That enchant does not exist"))
        }.replaceSuggestions(ArgumentSuggestions.strings {
            allEnchants.keys.toTypedArray()
        })

    private fun customItemArgument(nodeName: String = "custom-item"): Argument<CustomItem> =
        CustomArgument(StringArgument(nodeName)) { nameArg ->
            allItems[nameArg.input] ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That item does not exist"))
        }.replaceSuggestions(ArgumentSuggestions.strings {
            allItems.keys.toTypedArray()
        })
}
