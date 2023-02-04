package com.briarcraft.adventure.api.enchant

import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.ItemStack

abstract class AbstractEnchantment(key: NamespacedKey): Enchantment(key) {
    abstract val enchantName: String
    abstract val displayColor: TextColor
    abstract val usage: EnchantmentUsage

    final override fun translationKey() = "enchant.${key.namespace}.${key.key}"

    override fun isTreasure() = false
    override fun isTradeable() = false
    override fun isDiscoverable() = false

    override fun getItemTarget() = usage.target
    override fun canEnchantItem(item: ItemStack) = usage.canEnchant(item)
    override fun getActiveSlots() = usage.activeSlots

    override fun getRarity() = EnchantmentRarity.VERY_RARE

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory) = 0.0f

    final override fun getStartLevel() = 1

    @Deprecated("Deprecated in Java", ReplaceWith("displayName(level)"))
    final override fun getName() = enchantName

    final override fun displayName(level: Int): TextComponent {
        return if (level == 1 && maxLevel == 1) {
            Component.text(enchantName, Style.style(displayColor))
        } else if (level > 10) {
            Component.text("$enchantName $level", Style.style(displayColor))
        } else {
            Component.text("$enchantName ", Style.style(displayColor))
                .append(Component.translatable("enchantment.level.$level", Style.style(displayColor)))
        }
    }
}
