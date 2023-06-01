package com.briarcraft.rtw.util

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

const val CONTEXT_ORIGINAL = "ORIGINAL"
const val CONTEXT_INHERIT = "INHERIT"

val playerContextKey = NamespacedKey("rtw", "context")
val maxAllowanceKey = NamespacedKey("rtw", "max-allowance")

fun Player.getContext(default: String = CONTEXT_ORIGINAL) =
    persistentDataContainer.getOrDefault(playerContextKey, PersistentDataType.STRING, default)

fun Player.setContext(context: String) = persistentDataContainer.set(playerContextKey, PersistentDataType.STRING, context)

fun Player.clearContext() = persistentDataContainer.remove(playerContextKey)

fun Player.getMaxAllowance() = persistentDataContainer.get(maxAllowanceKey, PersistentDataType.INTEGER)

fun Player.setMaxAllowance(maxAllowance: Int?): Int? {
    val curMaxAllowance = persistentDataContainer.get(maxAllowanceKey, PersistentDataType.INTEGER)
    if (maxAllowance == null) {
        persistentDataContainer.remove(maxAllowanceKey)
    } else {
        persistentDataContainer.set(maxAllowanceKey, PersistentDataType.INTEGER, maxAllowance)
    }
    return curMaxAllowance
}
