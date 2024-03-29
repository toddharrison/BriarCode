package com.briarcraft.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.logging.Logger
import javax.sql.DataSource
import kotlin.coroutines.CoroutineContext

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DataSourceService(val logger: Logger, private val dataSource: DataSource) {
    val connection: Connection get() = dataSource.connection

    fun connection(): Connection = dataSource.connection

    suspend inline fun <T> useConnection(
        context: CoroutineContext = Dispatchers.IO,
        crossinline block: suspend (Connection) -> T
    ): T {
        return withContext(context) {
            connection.use { block(it) }
        }
    }

    suspend inline fun <T> useConnectionSafely(
        context: CoroutineContext = Dispatchers.IO,
        crossinline block: suspend (Connection) -> T
    ): Result<T> {
        return runCatching {
            useConnection(context, block)
        }
    }

    suspend inline fun <T> statement(
        @Language("MySQL") sql: String,
        crossinline transform: suspend (ResultSet) -> T
    ): T {
        return useConnectionSafely { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(sql).use { transform(it) }
            }
        }.onFailure {
            logger.severe { "Executing a statement failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun execute(@Language("MySQL") sql: String) = execute(sql) {}

    suspend inline fun execute(
        @Language("MySQL") sql: String,
        crossinline setParams: suspend (PreparedStatement) -> Unit
    ): Boolean {
        return useConnectionSafely { connection ->
            connection.prepareStatement(sql).use { statement ->
                setParams(statement)
                statement.execute()
            }
        }.onFailure {
            logger.severe { "Execution failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun <T> query(
        @Language("MySQL") sql: String,
        crossinline transform: suspend (ResultSet) -> T
    ): T = query(sql, {}, transform)

    suspend inline fun <T> query(
        @Language("MySQL") sql: String,
        crossinline setParams: suspend (PreparedStatement) -> Unit,
        crossinline transform: suspend (ResultSet) -> T
    ): T {
        return useConnectionSafely { connection ->
            connection.prepareStatement(sql).use { statement ->
                setParams(statement)
                statement.executeQuery().use { transform(it) }
            }
        }.onFailure {
            logger.severe { "Query failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun update(@Language("MySQL") sql: String): Int = update(sql) {}

    suspend inline fun update(
        @Language("MySQL") sql: String,
        crossinline setParams: suspend (PreparedStatement) -> Unit
    ): Int {
        return useConnectionSafely { connection ->
            connection.prepareStatement(sql).use { statement ->
                setParams(statement)
                statement.executeUpdate()
            }
        }.onFailure {
            logger.severe { "Update failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun <T> updateGetGeneratedKeys(
        @Language("MySQL") sql: String,
        crossinline transform: suspend (ResultSet) -> T
    ): T = updateGetGeneratedKeys(sql, {}, transform)

    suspend inline fun <T> updateGetGeneratedKeys(
        @Language("MySQL") sql: String,
        crossinline setParams: suspend (PreparedStatement) -> Unit,
        crossinline transform: suspend (ResultSet) -> T
    ): T {
        return useConnectionSafely { connection ->
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
                setParams(statement)
                statement.executeUpdate()
                statement.generatedKeys.use { rs ->
                    rs.next()
                    transform(rs)
                }
            }
        }.onFailure {
            logger.severe { "Update with generated keys failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun <T> batch(
        @Language("MySQL") sql: String,
        items: Iterable<T>,
        autoCommit: Boolean = false,
        crossinline setParams: suspend (PreparedStatement, T) -> Unit
    ): IntArray {
        return batch(sql, items.asSequence(), autoCommit, setParams)
    }

    suspend inline fun <T> batch(
        @Language("MySQL") sql: String,
        items: Sequence<T>,
        autoCommit: Boolean = false,
        crossinline setParams: suspend (PreparedStatement, T) -> Unit
    ): IntArray {
        return useConnectionSafely { connection ->
            if (!autoCommit) connection.autoCommit = false
            connection.prepareStatement(sql).use { statement ->
                items.forEach {
                    setParams(statement, it)
                    statement.addBatch()
                }
                statement.executeBatch()
            }.also {
                if (!autoCommit) {
                    connection.commit()
                    connection.autoCommit = true
                }
            }
        }.onFailure {
            logger.severe { "Batch failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun batch(
        sqlStatements: Sequence<String>
    ) {
        return useConnectionSafely { connection ->
            connection.autoCommit = false
            connection.createStatement().use { statement ->
                sqlStatements.forEach(statement::addBatch)
                statement.executeBatch()
            }
            connection.commit()
            connection.autoCommit = true
        }.onFailure {
            logger.severe { "Batch failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun call(
        @Language("MySQL") sql: String, // "{call procedureName(?, ?, ?)}}"
        crossinline setParams: suspend (PreparedStatement) -> Unit
    ): Boolean {
        return useConnectionSafely { connection ->
            connection.prepareCall(sql).use { statement ->
                setParams(statement)
                statement.execute()
            }
        }.onFailure {
            logger.severe { "Call failed: ${it.message}" }
        }.getOrThrow()
    }

    suspend inline fun <T> batchCall(
        @Language("MySQL") sql: String, // "{call procedureName(?, ?, ?)}}"
        items: Sequence<T>,
        autoCommit: Boolean = false,
        crossinline setParams: suspend (PreparedStatement, T) -> Unit
    ): IntArray {
        return useConnectionSafely { connection ->
            if (!autoCommit) connection.autoCommit = false
            connection.prepareCall(sql).use { statement ->
                items.forEach {
                    setParams(statement, it)
                    statement.addBatch()
                }
                statement.executeBatch()
            }.also {
                if (!autoCommit) {
                    connection.commit()
                    connection.autoCommit = true
                }
            }
        }.onFailure {
            logger.severe { "Batch call failed: ${it.message}" }
        }.getOrThrow()
    }
}
