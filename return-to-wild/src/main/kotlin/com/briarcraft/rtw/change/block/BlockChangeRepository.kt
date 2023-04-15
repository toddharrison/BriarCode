package com.briarcraft.rtw.change.block

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.change.repo.DependencyChange
import com.briarcraft.rtw.change.repo.DependencyChanges
import com.briarcraft.rtw.change.repo.BufferedChangeRepository
import com.briarcraft.rtw.util.CONTEXT_INHERIT
import com.briarcraft.rtw.util.map
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.World
import java.sql.Timestamp
import java.time.Instant

const val UPDATE_NEW_MATERIAL = "newMaterial"
const val UPDATE_NEW_CATEGORY = "newCategory"
const val UPDATE_TIMESTAMP = "timestamp"

@Suppress("DEPRECATION")
class BlockChangeRepository(
    override val server: Server,
    override val dataSource: DataSourceService,
    override val tableName: String = "rtw_block_change",
): BufferedChangeRepository<BlockChange>() {
    override suspend fun createTable() {
        dataSource.execute("""
            CREATE TABLE IF NOT EXISTS $tableName (
                context VARCHAR(44) NOT NULL,
                type VARCHAR(32) NOT NULL,
                world VARCHAR(32) NOT NULL,
                blockKey BIGINT NOT NULL,
                cause VARCHAR(64) NULL,
                causeName VARCHAR(64) NULL,
                x INT NOT NULL,
                y SMALLINT NOT NULL,
                z INT NOT NULL,
                material VARCHAR(64) NOT NULL,
                blockData VARCHAR(256) NOT NULL,
                category INT NOT NULL,
                newMaterial VARCHAR(64) NOT NULL,
                newCategory INT NOT NULL,
                timestamp TIMESTAMP NOT NULL,
                PRIMARY KEY (world, context, blockKey),
                UNIQUE (world, context, y, x, z)
            )""".trimIndent())
    }

    override suspend fun save(item: BlockChange) {
        require(item.location.y >= -512 && item.location.y <= 511)

        dataSource.execute("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
        """.trimIndent()) { statement ->
            statement.setString(1, item.context)
            statement.setString(2, item.type)
            statement.setString(3, item.location.world.name)
            statement.setLong(4, item.location.toBlockKey())
            statement.setString(5, item.cause?.asString())
            statement.setString(6, item.causeName)
            statement.setInt(7, item.location.blockX)
            statement.setInt(8, item.location.blockY)
            statement.setInt(9, item.location.blockZ)
            statement.setString(10, item.blockData.material.key.asString())
            statement.setString(11, item.blockData.asString)
            statement.setInt(12, item.category)
            statement.setString(13, item.newMaterial.key.asString())
            statement.setInt(14, item.newCategory)
            statement.setTimestamp(15, Timestamp.from(item.timestamp))
        }
    }

    override suspend fun saveAll(items: List<BlockChange>) {
        require(items.all { item -> item.location.y >= -512 && item.location.y <= 511 })

        dataSource.batch("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
        """.trimIndent(), items, false) { statement, item ->
            statement.setString(1, item.context)
            statement.setString(2, item.type)
            statement.setString(3, item.location.world.name)
            statement.setLong(4, item.location.toBlockKey())
            statement.setString(5, item.cause?.asString())
            statement.setString(6, item.causeName)
            statement.setInt(7, item.location.blockX)
            statement.setInt(8, item.location.blockY)
            statement.setInt(9, item.location.blockZ)
            statement.setString(10, item.blockData.material.key.asString())
            statement.setString(11, item.blockData.asString)
            statement.setInt(12, item.category)
            statement.setString(13, item.newMaterial.key.asString())
            statement.setInt(14, item.newCategory)
            statement.setTimestamp(15, Timestamp.from(item.timestamp))
        }
    }

    override suspend fun saveWherePresent(item: BlockChange, change: DependencyChange) {
        require(item.location.y >= -512 && item.location.y <= 511)
        require(change.location.y >= -512 && change.location.y <= 511)
        require(item.location.world == change.location.world)
        require(item.context == CONTEXT_INHERIT)

        val latestChange = findLatestChange(change)
        if (latestChange != null) {
            dataSource.execute("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
            """.trimIndent()) { statement ->
                statement.setString(1, latestChange.context)
                statement.setString(2, item.type)
                statement.setString(3, item.location.world.name)
                statement.setLong(4, item.location.toBlockKey())
                statement.setString(5, latestChange.cause)
                statement.setString(6, latestChange.causeName)
                statement.setInt(7, item.location.blockX)
                statement.setInt(8, item.location.blockY)
                statement.setInt(9, item.location.blockZ)
                statement.setString(10, item.blockData.material.key.asString())
                statement.setString(11, item.blockData.asString)
                statement.setInt(12, item.category)
                statement.setString(13, item.newMaterial.key.asString())
                statement.setInt(14, item.newCategory)
                statement.setTimestamp(15, Timestamp.from(item.timestamp))
            }
        }
    }

    override suspend fun saveAllWherePresent(items: List<BlockChange>, change: DependencyChange) {
        require(items.all { item -> item.location.y >= -512 && item.location.y <= 511 })
        require(change.location.y >= -512 && change.location.y <= 511)
        require(items.all { item -> item.location.world == change.location.world })
        require(items.all { item -> item.context == CONTEXT_INHERIT })

        val latestChange = findLatestChange(change)
        if (latestChange != null) {
            dataSource.batch("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
            """.trimIndent(), items, false) { statement, item ->
                statement.setString(1, latestChange.context)
                statement.setString(2, item.type)
                statement.setString(3, item.location.world.name)
                statement.setLong(4, item.location.toBlockKey())
                statement.setString(5, latestChange.cause)
                statement.setString(6, latestChange.causeName)
                statement.setInt(7, item.location.blockX)
                statement.setInt(8, item.location.blockY)
                statement.setInt(9, item.location.blockZ)
                statement.setString(10, item.blockData.material.key.asString())
                statement.setString(11, item.blockData.asString)
                statement.setInt(12, item.category)
                statement.setString(13, item.newMaterial.key.asString())
                statement.setInt(14, item.newCategory)
                statement.setTimestamp(15, Timestamp.from(item.timestamp))
            }
        }
    }

    override suspend fun saveWhereOnePresent(item: BlockChange, changes: DependencyChanges) {
        require(item.location.y >= -512 && item.location.y <= 511)
        require(changes.locations.all { location -> location.y >= -512 && location.y <= 511 })
        require(item.location.world == changes.world)
        require(item.context == CONTEXT_INHERIT)

        val latestChange = findLatestChange(changes)
        if (latestChange != null) {
            dataSource.execute("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
            """.trimIndent()) { statement ->
                statement.setString(1, latestChange.context)
                statement.setString(2, item.type)
                statement.setString(3, item.location.world.name)
                statement.setLong(4, item.location.toBlockKey())
                statement.setString(5, latestChange.cause)
                statement.setString(6, latestChange.causeName)
                statement.setInt(7, item.location.blockX)
                statement.setInt(8, item.location.blockY)
                statement.setInt(9, item.location.blockZ)
                statement.setString(10, item.blockData.material.key.asString())
                statement.setString(11, item.blockData.asString)
                statement.setInt(12, item.category)
                statement.setString(13, item.newMaterial.key.asString())
                statement.setInt(14, item.newCategory)
                statement.setTimestamp(15, Timestamp.from(item.timestamp))
            }
        }
    }

    override suspend fun saveAllWhereOnePresent(items: List<BlockChange>, changes: DependencyChanges) {
        require(items.all { item -> item.location.y >= -512 && item.location.y <= 511 })
        require(changes.locations.all { location -> location.y >= -512 && location.y <= 511 })
        require(items.all { item -> item.location.world == changes.world })
        require(items.all { item -> item.context == CONTEXT_INHERIT })

        val latestChange = findLatestChange(changes)
        if (latestChange != null) {
            dataSource.batch("""
            INSERT INTO $tableName
            (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
            """.trimIndent(), items, false) { statement, item ->
                statement.setString(1, latestChange.context)
                statement.setString(2, item.type)
                statement.setString(3, item.location.world.name)
                statement.setLong(4, item.location.toBlockKey())
                statement.setString(5, latestChange.cause)
                statement.setString(6, latestChange.causeName)
                statement.setInt(7, item.location.blockX)
                statement.setInt(8, item.location.blockY)
                statement.setInt(9, item.location.blockZ)
                statement.setString(10, item.blockData.material.key.asString())
                statement.setString(11, item.blockData.asString)
                statement.setInt(12, item.category)
                statement.setString(13, item.newMaterial.key.asString())
                statement.setInt(14, item.newCategory)
                statement.setTimestamp(15, Timestamp.from(item.timestamp))
            }
        }
    }

    override suspend fun update(item: BlockChange, data: Map<String, Any>) {
        require(item.location.y >= -512 && item.location.y <= 511)
        require(data.isNotEmpty())

        val newMaterial = data[UPDATE_NEW_MATERIAL] as Material? ?: item.newMaterial
        val newCategory = data[UPDATE_NEW_CATEGORY] as Int? ?: item.newCategory
        val timestamp = data[UPDATE_TIMESTAMP] as Instant? ?: item.timestamp

        dataSource.update("""
            UPDATE $tableName
            SET newMaterial = ?, newCategory = ?, timestamp = ?
            WHERE world = ?
            AND context = ?
            AND blockKey = ?
        """.trimIndent()) { statement ->
            statement.setString(1, newMaterial.key.asString())
            statement.setInt(2, newCategory)
            statement.setTimestamp(3, Timestamp.from(timestamp))
            statement.setString(4, item.location.world.name)
            statement.setString(5, item.context)
            statement.setLong(6, item.location.toBlockKey())
        }
    }

    override suspend fun findChanges(): List<BlockChange> {
        return dataSource.query("""
            SELECT context, type, cause, causeName, world, x, y, z, blockData, category, newMaterial, newCategory, timestamp
            FROM $tableName
        """.trimIndent()) { rs ->
            rs.map {
                val context = it.getString(1)
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val category = it.getInt(10)
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val newCategory = it.getInt(12)
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    override suspend fun findChanges(context: String): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val category = it.getInt(10)
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val newCategory = it.getInt(12)
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    suspend fun findChanges(context: String, cause: NamespacedKey, since: Instant): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
            AND a.cause = ?
            AND a.timestamp < ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setString(2, cause.asString())
            statement.setTimestamp(3, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val category = it.getInt(10)
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val newCategory = it.getInt(12)
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    suspend fun findByCategory(context: String, category: Int, since: Instant): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
            AND a.category = ?
            AND a.timestamp < ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setInt(2, category)
            statement.setTimestamp(3, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val newCategory = it.getInt(12)
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    suspend fun findByNewCategory(context: String, newCategory: Int, since: Instant): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
            AND a.newCategory = ?
            AND a.timestamp < ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setInt(2, newCategory)
            statement.setTimestamp(3, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val category = it.getInt(10)
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    suspend fun findByCategories(context: String, category: Int, newCategory: Int, since: Instant): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
            AND a.category = ?
            AND a.newCategory = ?
            AND a.timestamp < ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setInt(2, category)
            statement.setInt(3, newCategory)
            statement.setTimestamp(4, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))!!
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    suspend fun findByRegion(context: String, world: World, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): List<BlockChange> {
        return dataSource.query("""
            SELECT context, type, cause, causeName, world, x, y, z, blockData, category, newMaterial, newCategory, timestamp
            FROM $tableName
            WHERE context = ?
            AND world = ?
            AND x BETWEEN ? AND ?
            AND y BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setString(2, world.name)
            statement.setInt(3, minX)
            statement.setInt(4, maxX)
            statement.setInt(5, minY)
            statement.setInt(6, maxY)
            statement.setInt(7, minZ)
            statement.setInt(8, maxZ)
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val x = it.getInt(6)
                val y = it.getInt(7)
                val z = it.getInt(8)
                val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val blockData = server.createBlockData(it.getString(9))
                val category = it.getInt(10)
                val newMaterial = Material.matchMaterial(it.getString(11))!!
                val newCategory = it.getInt(12)
                val timestamp = it.getTimestamp(13).toInstant()
                BlockChange(context, type, cause, causeName, location, blockData, category, newMaterial, newCategory, timestamp)
            }
        }
    }

    override suspend fun deleteAll(): Int {
        return dataSource.update("""
            DELETE FROM $tableName
        """.trimIndent())
    }

    override suspend fun delete(context: String): Int {
        return dataSource.update("""
            DELETE FROM $tableName
            WHERE context = ?
        """.trimIndent()) { statement ->
            statement.setString(1, context)
        }
    }

    override suspend fun delete(item: BlockChange): Boolean {
        val blockKey = item.location.toBlockKey()
        return dataSource.update("""
            DELETE FROM $tableName
            WHERE context = ?
            AND world = ?
            AND blockKey = ?
        """.trimIndent()) { statement ->
            statement.setString(1, item.context)
            statement.setString(2, item.location.world.name)
            statement.setLong(3, blockKey)
        } == 1
    }

    override suspend fun deleteAll(items: List<BlockChange>) {
        // TODO Refactor into efficient query
        items.forEach { delete(it) }
    }
}
