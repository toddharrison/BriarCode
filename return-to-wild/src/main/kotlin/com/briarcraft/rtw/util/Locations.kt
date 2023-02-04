package com.briarcraft.rtw.util

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.plugin.Plugin

interface Locatable {
    val location: Location
}

data class LocatableWrapper<T>(
    val value: T,
    val getLocation: (T) -> Location
): Locatable {
    override val location: Location
        get() = getLocation(value)
}

fun <T> asLocatable(value: T, getLocation: (T) -> Location): LocatableWrapper<T> {
    return LocatableWrapper(value, getLocation)
}

suspend fun <T: Locatable> Iterable<T>.processByChunk(plugin: Plugin, action: suspend (T) -> Unit) {
    groupBy { it.location.chunk }.forEach { (chunk, elms) ->
        plugin.launch {
            chunk.addPluginChunkTicket(plugin)
            elms.forEach { action(it) }
            chunk.removePluginChunkTicket(plugin)
        }
        delay(10.ticks)
    }
}
