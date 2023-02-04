package com.briarcraft.adventure.api

import org.bukkit.enchantments.Enchantment

const val BYTE_TRUE = 1.toByte()
const val BYTE_FALSE = 0.toByte()

fun registerEnchantment(enchantment: Enchantment) {
    unlockEnchantmentRegistration()
    Enchantment.registerEnchantment(enchantment)
}

fun registerEnchantments(vararg enchantments: Enchantment) {
    unlockEnchantmentRegistration()
    enchantments.forEach { enchantment -> Enchantment.registerEnchantment(enchantment) }
}

private fun unlockEnchantmentRegistration() {
    if (!Enchantment.isAcceptingRegistrations()) {
        val field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, true)
    }
}
