package com.briarcraft.datasource

import kotlinx.coroutines.*
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.logging.Logger
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DataSynchronizationService<T>(
    private val logger: Logger,
    private val dataCacheService: DataCacheService<T>,
    private val maxFileCacheCount: Int,
    private val checkDelay: Duration = 5.seconds,
    private val staleDelaySeconds: Int = 60,
    private val syncDelay: Duration = 10.seconds,
    private val doLog: Boolean = true,
) {
    init {
        require(maxFileCacheCount > 0)
    }

//    private val cacheFileScope = CoroutineScope(Job() + Dispatchers.IO)
    private val cacheFileContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val dbSyncScope = CoroutineScope(Job() + Dispatchers.IO)
    private val checkScope = CoroutineScope(Job() + Dispatchers.Default)
    private var openCacheFile: File? = null
    private var running: Boolean = false
    private var lastWrite: Instant? = null
    private var flushed: Boolean = true

    suspend fun start(): Boolean {
        return withContext(cacheFileContext) {
            if (!running) {
                if (doLog) logger.info("Starting")
                running = true
                flushed = true
                openCacheFile = dataCacheService.openCache()
                if (doLog) logger.info("Opening new cache file ${openCacheFile!!.name}")
                scheduleCloseWrittenFilesAfterInactivity()
                scheduleSyncToDatabase()
                true
            } else false
        }
    }

    suspend fun stop(): Boolean {
        return withContext(cacheFileContext) {
            if (running) {
                if (doLog) logger.info("Stopping")
                running = false
                lastWrite = null
                if (openCacheFile != null) {
                    if (doLog) logger.info("Closing cache file ${openCacheFile!!.name}")
                    dataCacheService.closeCache()
                    openCacheFile = null
                }
                true
            } else false
        }
    }

    suspend fun write(data: T): Boolean {
        return withContext(cacheFileContext) {
            if (!running) return@withContext false
//            if (doLog) logger.info("Writing to cache file: 1")
            dataCacheService.writeToCache(data).also { written ->
                if (written && dataCacheService.getItemsInCache() >= maxFileCacheCount) cycleToNextCacheFile()
                else lastWrite = Instant.now()
                flushed = false
            }
        }
    }

    suspend fun write(data: Iterable<T>): Boolean {
        return write(data.asSequence())
    }

    suspend fun write(data: Sequence<T>): Boolean {
        return withContext(cacheFileContext) {
            if (!running) return@withContext false

            val remainder = max(maxFileCacheCount - dataCacheService.getItemsInCache(), 0)
            data.take(remainder).let { elements ->
//                if (doLog) logger.info("Writing to cache file: ${elements.count()}")
                dataCacheService.writeToCache(elements)
            }
            data.drop(remainder).chunked(maxFileCacheCount).forEach { elements ->
                cycleToNextCacheFile()
//                if (doLog) logger.info("Writing to cache file: ${elements.count()}")
                dataCacheService.writeToCache(elements)
            }
            if (dataCacheService.getItemsInCache() == maxFileCacheCount) {
                if (doLog) logger.info("Cache file was filled: ${openCacheFile?.name}")
                cycleToNextCacheFile()
            }
            lastWrite = Instant.now()
            flushed = false

            true
        }
    }

    private fun scheduleCloseWrittenFilesAfterInactivity() {
        checkScope.launch {
            while (running) {
                delay(checkDelay)
                try {
                    if (lastWrite != null) {
                        if (lastWrite?.until(Instant.now(), ChronoUnit.SECONDS)!! > staleDelaySeconds) {
                            if (doLog) logger.info("Stale delay exceeded, closing cache file: ${openCacheFile?.name}")
                            cycleToNextCacheFile()
                        } else if (!flushed) {
//                            if (doLog) logger.info("Flushing buffer to file: ${openCacheFile?.name}")
                            flushed = true
                            dataCacheService.flushCache()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun scheduleSyncToDatabase() {
        dbSyncScope.launch {
            while (running) {
                delay(syncDelay)
                try {
                    val file = dataCacheService.writeCacheToDatabase()
                    if (doLog && file != null) logger.info("Wrote to the database: ${file.name}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun cycleToNextCacheFile() {
        if (doLog) logger.info("Closing cache file: ${openCacheFile!!.name}")
        dataCacheService.closeCache()
        lastWrite = null
        openCacheFile = dataCacheService.openCache()
        if (doLog) logger.info("Opened new cache file: ${openCacheFile!!.name}")
    }
}
