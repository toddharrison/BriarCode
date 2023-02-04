package com.briarcraft.rtw.change.repo

import com.briarcraft.rtw.repo.Repository
import com.briarcraft.rtw.util.map
import org.bukkit.Location

@Suppress("DEPRECATION")
interface ChangeRepository<T>: Repository {
    suspend fun save(item: T)
    suspend fun saveAll(items: List<T>)
    suspend fun saveWherePresent(item: T, change: DependencyChange)
    suspend fun saveAllWherePresent(items: List<T>, change: DependencyChange)
    suspend fun saveWhereOnePresent(item: T, changes: DependencyChanges)
    suspend fun saveAllWhereOnePresent(items: List<T>, changes: DependencyChanges)

    suspend fun update(item: T, data: Map<String, Any>)

    suspend fun findChanges(): List<T>
    suspend fun findChanges(context: String): List<T>

    suspend fun findContexts(change: DependencyChange): Set<String> {
        return dataSource.query("""
            SELECT context FROM $tableName
            WHERE world=?
            AND blockKey=?
        """.trimIndent(), {statement ->
            statement.setString(1, change.location.world.name)
            statement.setLong(2, change.location.toBlockKey())
        }) { rs ->
            rs.map { it.getString(1) }.toSet()
        }
    }

    suspend fun findContexts(changes: DependencyChanges): Set<String> {
        if (changes.locations.isEmpty()) return setOf()
        require(changes.locations.isNotEmpty())
        return dataSource.statement(String.format("""
            SELECT context FROM $tableName
            WHERE world='${changes.world.name}'
            AND blockKey IN (%s)
        """.trimIndent(), changes.locations.map(Location::toBlockKey).joinToString(","))) { rs ->
            rs.map { it.getString(1) }.toSet()
        }
    }

    suspend fun findLatestChange(change: DependencyChange): LatestChange? {
        return dataSource.query("""
            SELECT context, cause, causeName FROM $tableName WHERE world=? AND blockKey=?
            ORDER BY timestamp DESC
            LIMIT 1
        """.trimIndent(), {statement ->
            statement.setString(1, change.location.world.name)
            statement.setLong(2, change.location.toBlockKey())
        }) { rs ->
            if (rs.next()) LatestChange(rs.getString(1), rs.getString(2), rs.getString(3)) else null
        }
    }

    suspend fun findLatestChange(changes: DependencyChanges): LatestChange? {
        if (changes.locations.isEmpty()) return null
        require(changes.locations.isNotEmpty())
        return dataSource.statement(String.format("""
            SELECT context, cause, causeName FROM $tableName
            WHERE world='${changes.world.name}'
            AND blockKey IN (%s)
            ORDER BY timestamp DESC
            LIMIT 1
        """.trimIndent(), changes.locations.map(Location::toBlockKey).joinToString(","))) { rs ->
            if (rs.next()) LatestChange(rs.getString(1), rs.getString(2), rs.getString(3)) else null
        }
    }

    suspend fun deleteAll(): Int
    suspend fun delete(context: String): Int
    suspend fun delete(item: T): Boolean
    suspend fun deleteAll(items: List<T>)
}
