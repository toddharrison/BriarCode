package com.briarcraft.regionscan

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.event.HandlerList
import java.io.FileWriter
import kotlin.math.roundToInt

@Suppress("unused")
class RegionScanPlugin : SuspendingJavaPlugin() {
    private var featureWriter: FileWriter? = null

    override suspend fun onLoadAsync() {
        saveDefaultConfig()
    }

    override suspend fun onEnableAsync() {
        withContext(Dispatchers.IO) {
            featureWriter = FileWriter("features.tsv", true)
        }

        server.pluginManager.registerSuspendingEvents(FeatureListener(this) { feature ->
            withContext(Dispatchers.IO) {
                featureWriter?.write("${feature.type}\t${feature.key}\t${feature.subKey ?: ""}\t${feature.location.world.name}\t${feature.location.x.roundToInt()}\t${feature.location.y.roundToInt()}\t${feature.location.z.roundToInt()}\t${feature.facing ?: ""}\n")
            }
        }, this)
    }

    override suspend fun onDisableAsync() {
        HandlerList.unregisterAll(this)

        withContext(Dispatchers.IO) {
            featureWriter?.close()
        }
    }
}
