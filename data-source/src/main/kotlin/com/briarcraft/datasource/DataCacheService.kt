package com.briarcraft.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.sql.PreparedStatement
import kotlin.coroutines.CoroutineContext

class DataCacheService<T>(
    private val dataSourceService: DataSourceService,
    @Language("MySQL") private val sql: String,
    private val rootDir: File,
    private val cachePrefix: String,
    private val cacheFileExtension: String,
    private val serializer: (T) -> Sequence<Any>,
    private val context: CoroutineContext = Dispatchers.IO,
    private val setParams: suspend (PreparedStatement, List<String>) -> Unit
) {
    init {
        if (rootDir.exists()) require(rootDir.isDirectory) else rootDir.mkdirs()
    }

    private var cacheFilename: String? = null
    private var cacheFile: File? = null
    private var cacheWriter: BufferedWriter? = null
    private var itemsInCache = 0

    fun getItemsInCache() = itemsInCache

    suspend fun openCache(): File {
        return withContext(context) {
            cacheWriter?.close()
            cacheFilename = "$cachePrefix-${System.currentTimeMillis()}.$cacheFileExtension"
            val file = File(rootDir, cacheFilename!!)
            cacheFile = file
            cacheWriter = FileOutputStream(file).bufferedWriter()
            file
        }
    }

    suspend fun writeToCache(data: T): Boolean {
        return withContext(context) {
            cacheWriter?.let { writer ->
                writer.write(serializer(data).joinToString("\t") + "\n")
                itemsInCache++
                true
            } ?: false
        }
    }

    suspend fun writeToCache(data: Iterable<T>): Boolean {
        return writeToCache(data.asSequence())
    }

    suspend fun writeToCache(data: Sequence<T>): Boolean {
        return withContext(context) {
            cacheWriter?.let { writer ->
                data.forEach {
                    writer.write(serializer(it).joinToString("\t") + "\n")
                    itemsInCache++
                }
                true
            } ?: false
        }
    }

    suspend fun closeCache(): File? {
        return withContext(context) {
            cacheWriter?.close()
            cacheWriter = null
            cacheFilename = null
            itemsInCache = 0

            val file = cacheFile
            cacheFile = null
            file
        }
    }

    suspend fun writeCacheToDatabase(): File? {
        return withContext(context) {
            val nextCacheFilename = rootDir
                .list { _, name ->
                    name != cacheFilename && name.startsWith(cachePrefix) && name.endsWith(cacheFileExtension)
                }?.minOrNull()
            if (nextCacheFilename != null) {
                val file = File(rootDir, nextCacheFilename)
                dataSourceService.useConnectionSafely { connection ->
                    connection.autoCommit = false
                    connection.prepareCall(sql).use { statement ->
                        FileInputStream(file).bufferedReader().use { reader ->
                            reader.lineSequence().forEach { line ->
                                setParams(statement, line.split("\t"))
                                statement.addBatch()
                            }
                        }
                        statement.executeBatch()
                    }.also {
                        connection.commit()
                        connection.autoCommit = true
                    }
                }.onSuccess {
                    file.delete()
                }.getOrThrow()
                file
            } else null
        }
    }
}
