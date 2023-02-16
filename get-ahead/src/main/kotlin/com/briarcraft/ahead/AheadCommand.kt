package com.briarcraft.ahead

import com.destroystokyo.paper.profile.ProfileProperty
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import org.betonquest.betonquest.item.QuestItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import java.nio.charset.StandardCharsets
import java.util.*


class AheadCommand {
    private val aheadPermission = "briar.ahead"

    fun register(plugin: Plugin) {
        CommandAPICommand("ahead")
            .withPermission(aheadPermission)

            // /give Rusli minecraft:player_head{display:{Name:"{\"text\":\"Ghost Ducky\"}"},SkullOwner:{Id:[I;1722500926,-786936235,-1760083171,-25531972],Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwNmIxOGQzZGZlZGFiNDQ0NjZlMGE3NGUxNTVhOGYyMTc4NzIwNDBhMDg1NTIwYTVhMGYzMGU4Y2QxODg1YyJ9fX0="}]}}} 1

            .withSubcommand(
                CommandAPICommand("test")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val item = player.inventory.itemInMainHand
                        val instructions: String = QuestItem.itemToString(item)
                        println(instructions)

                        val meta = item.itemMeta
                        if (meta is SkullMeta) {
                            val displayName = meta.displayName()
                            val owningPlayer = meta.owningPlayer
                            val playerUniqueId = owningPlayer?.uniqueId

                            println(displayName)
                            println(owningPlayer)
                            println(playerUniqueId)

//                            val textures = meta.owningPlayer?.properties?.find { it.name == "textures" }?.value
//
//                            if (displayName != null && playerUniqueId != null && textures != null) {
//                                val item2 = createHead(plugin, displayName, playerUniqueId, textures)
//                                player.inventory.addItem(item2)
//                            }
                        }
//                        val meta = item.itemMeta
//                        if (meta is SkullMeta) {
//                            val displayName = meta.displayName()
//                            val playerUniqueId = meta.playerProfile?.id
//                            val textures = meta.playerProfile?.properties?.find { it.name == "textures" }?.value
//
//                            if (displayName != null && playerUniqueId != null && textures != null) {
//                                val item2 = createHead(plugin, displayName, playerUniqueId, textures)
//                                player.inventory.addItem(item2)
//                            }
//                        }
                    }))
            .withSubcommand(
                CommandAPICommand("create")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val displayName = Component.text("Rune Stone")
                        val playerUniqueId = UUID.fromString("bdc273dd-a63e-44e0-873b-d76a145fa624")
                        val textures = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTIyMjg3NjVkZjBlMmViZDZjM2EzZmRlMDcwZGRjOGM1NTFjZWI0YTQzYjk1OWUxYzQ0ZDM0OWNlNTE2NTYwIn19fQ=="

                        val item = createHead(plugin, displayName, playerUniqueId, textures)
                        player.inventory.addItem(item)
                    }))
            .withSubcommand(
                CommandAPICommand("info")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val item = player.inventory.itemInMainHand
                        val meta = item.itemMeta

//                        NamespacedKey(ProtectionStones.getInstance(), "isPSBlock"),
//                        PersistentDataType.BYTE

//                        val tagContainer = item.itemMeta.customTagContainer
//                        val isPSBlock: Byte? = tagContainer.getCustomTag(
//                            NamespacedKey("protectionstones", "isPSBlock"),
//                            ItemTagType.BYTE
//                        )
//                        val tag = isPSBlock != null && isPSBlock.toInt() == 1

                        val pdc = meta.persistentDataContainer
                        player.sendMessage("TEST: " + serialize(pdc))



//                        // *** FIX ***
//                        meta.persistentDataContainer.set(NamespacedKey("protectionstones", "ispsblock"), PersistentDataType.BYTE, 1.toByte())

                        item.itemMeta = meta
//                        player.sendMessage("$item")
                    }))
            .register()
    }

    fun unregister() {
        CommandAPI.unregister("get-ahead")
    }

    private fun createHead(plugin: Plugin, displayName: Component, playerUniqueId: UUID, textures: String): ItemStack {
        val playerProfile = plugin.server.createProfile(playerUniqueId)
        playerProfile.properties.add(ProfileProperty("textures", textures))

        val item2 = ItemStack(Material.PLAYER_HEAD)
        val meta2 = item2.itemMeta as SkullMeta
        meta2.displayName(displayName)
        meta2.playerProfile = playerProfile
        item2.itemMeta = meta2

        return item2
    }



    fun serialize(pdc: PersistentDataContainer): String? {
        return Base64.getEncoder().encodeToString(pdc.serializeToBytes())
    }

    fun deserialize(data: String): Map<String, String> {
        return java.util.Map.of()
    }
}