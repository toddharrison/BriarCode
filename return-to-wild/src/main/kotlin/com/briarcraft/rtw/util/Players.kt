package com.briarcraft.rtw.util

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

const val CONTEXT_ORIGINAL = "ORIGINAL"
const val CONTEXT_INHERIT = "INHERIT"

val playerContextKey = NamespacedKey("rtw", "context")

fun Player.getContext(default: String = CONTEXT_ORIGINAL) =
    persistentDataContainer.getOrDefault(playerContextKey, PersistentDataType.STRING, default)

fun Player.setContext(context: String) = persistentDataContainer.set(playerContextKey, PersistentDataType.STRING, context)

fun Player.clearContext() = persistentDataContainer.remove(playerContextKey)
