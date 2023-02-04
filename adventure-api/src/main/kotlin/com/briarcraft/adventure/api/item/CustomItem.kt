package com.briarcraft.adventure.api.item

import com.briarcraft.adventure.api.BYTE_TRUE
import com.briarcraft.adventure.api.enchant.updateEnchantingLore
import com.briarcraft.kotlin.util.itemStackOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

interface CustomItem {
    val key: NamespacedKey
    val type: Material
    val name: String
    val nameStyle: Style?
    val unbreakable: Boolean
    val description: List<Component>
    val enchants: Map<Enchantment, Int>

    fun create(): ItemStack {
        val item = itemStackOf(type, Component.text(name, nameStyle ?: Style.empty()))

        item.itemMeta.let { meta ->
            meta.persistentDataContainer.set(key, PersistentDataType.BYTE, BYTE_TRUE)
            meta.isUnbreakable = unbreakable
            item.itemMeta = meta
        }

        if (enchants.isNotEmpty()) {
            item.addUnsafeEnchantments(enchants)
            updateEnchantingLore(item)
        }

        if (description.isNotEmpty()) {
            item.itemMeta.let { meta ->
                val lore = meta.lore() ?: mutableListOf()
                if (enchants.isNotEmpty()) lore.add(Component.text(""))
                lore.addAll(description)
                meta.lore(lore)
                item.itemMeta = meta
            }
        }

        return item
    }

    fun isInstance(item: ItemStack): Boolean {
        return if (item.itemMeta != null) {
            isInstance(item.itemMeta)
        } else false
    }

    fun isInstance(itemMeta: ItemMeta): Boolean {
        return itemMeta.persistentDataContainer.get(key, PersistentDataType.BYTE) == BYTE_TRUE
    }
}
