package com.briarcraft.rtw.config

import org.bukkit.configuration.Configuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class DataSynchronizationConfig(
    val maxFileCacheCount: Int = 500,
    val checkDelay: Duration = 10.seconds,
    val staleDelay: Duration = 60.seconds,
    val syncDelay: Duration = 60.seconds,
    val doLog: Boolean = true,
)

fun loadDataSynchronizationConfig(config: Configuration): DataSynchronizationConfig {
    val maxFileCacheCount = config.getInt("synchronization.max-cache-size", 500)
    val checkDelay = config.getInt("synchronization.max-unchanged-cache-age-in-seconds", 5).seconds
    val staleDelay = config.getInt("synchronization.frequency-to-check-max-age-in-seconds", 1).seconds
    val syncDelay = config.getInt("synchronization.frequency-to-write-cache-to-database-in-seconds", 10).seconds
    val doLog = config.getBoolean("synchronization.log-cache-synchronization", false)
    return DataSynchronizationConfig(maxFileCacheCount, checkDelay, staleDelay, syncDelay, doLog)
}
