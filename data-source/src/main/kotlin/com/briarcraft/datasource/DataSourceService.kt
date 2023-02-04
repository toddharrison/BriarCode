package com.briarcraft.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.logging.Logger
import javax.sql.DataSource

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DataSourceService(private val logger: Logger, private val dataSource: DataSource) {
    fun connection(): Connection = dataSource.connection

    suspend fun <T> statement(sql: String, action: suspend (ResultSet) -> T): T {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
                    connection.createStatement().use { statement ->
                        statement.executeQuery(sql).use { action(it) }
                    }
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }

    suspend fun execute(sql: String, setParams: suspend (PreparedStatement) -> Unit = {}) {
        withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
                    connection.prepareStatement(sql).use { statement ->
                        setParams(statement)
                        statement.execute()
                    }
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }

    suspend fun <T> query(sql: String, setParams: suspend (PreparedStatement) -> Unit = {}, action: suspend (ResultSet) -> T): T {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
                    connection.prepareStatement(sql).use { statement ->
                        setParams(statement)
                        statement.executeQuery().use { action(it) }
                    }
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }

    suspend fun update(sql: String, setParams: suspend (PreparedStatement) -> Unit = {}): Int {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
                    connection.prepareStatement(sql).use { statement ->
                        setParams(statement)
                        statement.executeUpdate()
                    }
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }

    suspend fun <T> updateGetGeneratedKeys(sql: String, setParams: suspend (PreparedStatement) -> Unit = {}, action: suspend (ResultSet) -> T): T {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
                        setParams(statement)
                        statement.executeUpdate()
                        statement.generatedKeys.use { rs ->
                            rs.next()
                            action(rs)
                        }
                    }
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }

    suspend fun <T> batch(sql: String, items: Iterable<T>, autoCommit: Boolean = true, setParams: suspend (PreparedStatement, T) -> Unit): IntArray {
        return batch(sql, items.asSequence(), autoCommit, setParams)
    }

    suspend fun <T> batch(sql: String, items: Sequence<T>, autoCommit: Boolean = true, setParams: suspend (PreparedStatement, T) -> Unit): IntArray {
        return withContext(Dispatchers.IO) {
            try {
                dataSource.connection.use { connection ->
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
                }
            } catch (e: Exception) {
                logger.severe("FAILED: $sql")
                throw e
            }
        }
    }
}
