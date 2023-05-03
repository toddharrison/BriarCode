package com.briarcraft.datasource

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
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

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var openCacheFile: File? = null
    private var running: Boolean = false
    private var lastWrite: Instant? = null
    private var flushed: Boolean = true

    private val writerChannel = Channel<T>(capacity = Channel.UNLIMITED).also { channel ->
        scope.launch {
            for (msg in channel) internalWrite(msg)
        }
    }

    private var staleCheckJob: Job? = null
    private var dbSyncJob: Job? = null

    suspend fun start(): Boolean {
        return if (!running) {
            if (doLog) logger.info("Starting")
            running = true
            flushed = true
            openCacheFile = dataCacheService.openCache()
            if (doLog) logger.info("Opening new cache file ${openCacheFile!!.name}")
            staleCheckJob = scheduleCloseWrittenFilesAfterInactivity()
            dbSyncJob = scheduleSyncToDatabase()
            true
        } else false
    }

    suspend fun stop(): Boolean {
        return if (running) {
            if (doLog) logger.info("Stopping")
            staleCheckJob?.cancel()
            staleCheckJob = null
            dbSyncJob?.cancel()
            dbSyncJob = null
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

    suspend fun write(data: T) {
        writerChannel.send(data)
    }

    suspend fun write(data: Iterable<T>) {
        data.forEach { writerChannel.send(it) }
    }

    suspend fun write(data: Sequence<T>) {
        data.forEach { writerChannel.send(it) }
    }

    private suspend fun internalWrite(data: T): Boolean {
        if (!running) return false
//        if (doLog) logger.info("Writing to cache file: 1")
        return dataCacheService.writeToCache(data).also { written ->
            if (written && dataCacheService.getItemsInCache() >= maxFileCacheCount) cycleToNextCacheFile()
            else lastWrite = Instant.now()
            flushed = false
        }
    }

    private fun scheduleCloseWrittenFilesAfterInactivity(): Job {
        return scope.launch {
            while (running) {
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
                delay(checkDelay)
            }
        }
    }

    private fun scheduleSyncToDatabase(): Job {
        return scope.launch {
            while (running) {
                try {
                    val file = dataCacheService.writeCacheToDatabase()
                    if (doLog && file != null) logger.info("Wrote to the database: ${file.name}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(syncDelay)
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
