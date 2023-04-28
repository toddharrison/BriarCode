package com.briarcraft.datasource

import kotlinx.coroutines.*
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit
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
) {
    init {
        require(maxFileCacheCount > 0)
    }

    private val cacheFileScope = CoroutineScope(Job() + Dispatchers.IO)
    private val dbSyncScope = CoroutineScope(Job() + Dispatchers.IO)
    private val checkScope = CoroutineScope(Job() + Dispatchers.Default)
    private var openCacheFile: File? = null
    private var running: Boolean = false
    private var lastWrite: Instant? = null
    private var flushed: Boolean = true

    suspend fun start(): Boolean {
        return withContext(cacheFileScope.coroutineContext) {
            if (!running) {
                logger.info("Starting")
                running = true
                flushed = true
                openCacheFile = dataCacheService.openCache()
                logger.info("Opening new cache file ${openCacheFile!!.name}")
                scheduleCloseWrittenFilesAfterInactivity()
                scheduleSyncToDatabase()
                true
            } else false
        }
    }

    suspend fun stop(): Boolean {
        return withContext(cacheFileScope.coroutineContext) {
            if (running) {
                logger.info("Stopping")
                running = false
                lastWrite = null
                if (openCacheFile != null) {
                    logger.info("Closing cache file ${openCacheFile!!.name}")
                    dataCacheService.closeCache()
                    openCacheFile = null
                }
                true
            } else false
        }
    }

    suspend fun write(data: T): Boolean {
        return withContext(cacheFileScope.coroutineContext) {
            if (!running) return@withContext false
            logger.info("Writing to cache file $data")
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
        return withContext(cacheFileScope.coroutineContext) {
            if (!running) return@withContext false

            val remainder = max(maxFileCacheCount - dataCacheService.getItemsInCache(), 0)
            data.take(remainder).let { elements ->
                logger.info("Writing to close the cache file ${elements.toList()}")
                dataCacheService.writeToCache(elements)
            }
            data.drop(remainder).chunked(maxFileCacheCount).forEach { elements ->
                cycleToNextCacheFile()
                logger.info("Writing to new cache file $elements")
                dataCacheService.writeToCache(elements)
            }
            if (dataCacheService.getItemsInCache() == maxFileCacheCount) {
                logger.info("Cache file was filled")
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
                            logger.info("Stale delay exceeded, closing cache file")
                            cycleToNextCacheFile()
                        } else if (!flushed) {
                            logger.info("Flushing buffer to file")
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
                    if (file != null) logger.info("Wrote ${file.name} to the database")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun cycleToNextCacheFile() {
        logger.info("Closing cache file ${openCacheFile!!.name}")
        dataCacheService.closeCache()
        lastWrite = null
        openCacheFile = dataCacheService.openCache()
        logger.info("Opened new cache file ${openCacheFile!!.name}")
    }
}
