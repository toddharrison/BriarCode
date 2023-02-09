package com.briarcraft.ahead

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

@Suppress("unused")
class AheadPlugin: SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        saveDefaultConfig()
    }

    override suspend fun onEnableAsync() {
    }

    override suspend fun onDisableAsync() {
    }
}
