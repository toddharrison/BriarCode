package com.briarcraft.rtw.change.block

import com.briarcraft.datasource.DataCacheService
import com.briarcraft.datasource.DataSourceService
import com.briarcraft.datasource.DataSynchronizationService
import com.briarcraft.rtw.change.repo.DependencyChange
import com.briarcraft.rtw.change.repo.DependencyChanges
import com.briarcraft.rtw.config.DataSynchronizationConfig
import com.briarcraft.rtw.repo.Repository
import com.briarcraft.rtw.util.map
import org.bukkit.*
import java.io.File
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val UPDATE_NEW_MATERIAL = "newMaterial"
const val UPDATE_NEW_CATEGORY = "newCategory"
const val UPDATE_TIMESTAMP = "timestamp"

@Suppress("DEPRECATION")
class BlockChangeRepository(
    override val server: Server,
    override val dataSource: DataSourceService,
    cacheDir: File,
    dataSynchronizationConfig: DataSynchronizationConfig,
    override val tableName: String = "rtw_block_change",
): Repository {
    private val timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        .withZone(ZoneId.systemDefault())

    private val dataCacheService = DataCacheService<ChangeEntity>(dataSource,
        "CALL ${tableName}_change(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        cacheDir,
        "block_change",
        "tsv",
        { sequenceOf(
            it.context,
            it.type,
            it.world,
            it.blockKey,
            it.cause,
            it.causeName,
            it.x,
            it.y,
            it.z,
            it.material,
            it.blockData,
            it.category,
            it.newMaterial,
            it.newCategory,
            if (it.timestamp == null) null else timestampFormat.format(it.timestamp),
            it.blockKey1,
            it.blockKey2,
            it.blockKey3,
            it.blockKey4,
            it.blockKey5,
            it.blockKey6,
            it.blockKey7,
            it.blockKey8,
            it.blockKey9,
            it.blockKey10,
        ) }
    ) { statement, data ->
        statement.setObject(1, data[0], Types.VARCHAR)
        statement.setObject(2, data[1], Types.VARCHAR)
        statement.setObject(3, data[2], Types.VARCHAR)
        statement.setObject(4, data[3], Types.BIGINT)
        statement.setObject(5, data[4], Types.VARCHAR)
        statement.setObject(6, data[5], Types.VARCHAR)
        statement.setObject(7, data[6], Types.INTEGER)
        statement.setObject(8, data[7], Types.SMALLINT)
        statement.setObject(9, data[8], Types.INTEGER)
        statement.setObject(10, data[9], Types.VARCHAR)
        statement.setObject(11, data[10], Types.VARCHAR)
        statement.setObject(12, data[11], Types.INTEGER)
        statement.setObject(13, data[12], Types.VARCHAR)
        statement.setObject(14, data[13], Types.INTEGER)
        statement.setObject(15, data[14], Types.TIMESTAMP)
        statement.setObject(16, data[15], Types.BIGINT)
        statement.setObject(17, data[16], Types.BIGINT)
        statement.setObject(18, data[17], Types.BIGINT)
        statement.setObject(19, data[18], Types.BIGINT)
        statement.setObject(20, data[19], Types.BIGINT)
        statement.setObject(21, data[20], Types.BIGINT)
        statement.setObject(22, data[21], Types.BIGINT)
        statement.setObject(23, data[22], Types.BIGINT)
        statement.setObject(24, data[23], Types.BIGINT)
        statement.setObject(25, data[24], Types.BIGINT)
    }
    private val dataSyncService = DataSynchronizationService(
        server.logger,
        dataCacheService,
        dataSynchronizationConfig.maxFileCacheCount,
        dataSynchronizationConfig.checkDelay,
        dataSynchronizationConfig.staleDelay.inWholeSeconds.toInt(),
        dataSynchronizationConfig.syncDelay,
        dataSynchronizationConfig.doLog
    )

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
        dataSource.execute("CREATE INDEX IF NOT EXISTS ${tableName}_timestamp ON $tableName (timestamp)")
        dataSource.execute("""
            CREATE OR REPLACE PROCEDURE ${tableName}_change(
                IN e_context VARCHAR(44),
                IN e_type VARCHAR(32),
                IN e_world VARCHAR(32),
                IN e_blockKey BIGINT,
                IN e_cause VARCHAR(64),
                IN e_causeName VARCHAR(64),
                IN e_x INT,
                IN e_y SMALLINT,
                IN e_z INT,
                IN e_material VARCHAR(64),
                IN e_blockData VARCHAR(256),
                IN e_category INT,
                IN e_newMaterial VARCHAR(64),
                IN e_newCategory INT,
                IN e_timestamp TIMESTAMP,
            
                IN e_blockKey_1 BIGINT,
                IN e_blockKey_2 BIGINT,
                IN e_blockKey_3 BIGINT,
                IN e_blockKey_4 BIGINT,
                IN e_blockKey_5 BIGINT,
                IN e_blockKey_6 BIGINT,
                IN e_blockKey_7 BIGINT,
                IN e_blockKey_8 BIGINT,
                IN e_blockKey_9 BIGINT,
                IN e_blockKey_10 BIGINT
            )
            BEGIN
                IF e_context IS NULL THEN
                    INSERT INTO $tableName
                        (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                        (SELECT t2.context, e_type, t2.world, e_blockKey, t2.cause, t2.causeName, e_x, e_y, e_z, e_material, e_blockData, e_category, e_newMaterial, e_newCategory, e_timestamp
                            FROM $tableName AS t2
                            WHERE world = e_world
                                AND blockKey IN (e_blockKey_1, e_blockKey_2, e_blockKey_3, e_blockKey_4, e_blockKey_5, e_blockKey_6, e_blockKey_7, e_blockKey_8, e_blockKey_9, e_blockKey_10)
                            ORDER BY timestamp DESC
                            LIMIT 1
                        )
                        ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp);
                ELSEIF e_newMaterial IS NULL THEN
                    DELETE FROM $tableName
                        WHERE world = e_world
                            AND context = e_context
                            AND blockKey = e_blockKey;
                ELSEIF e_type IS NULL THEN
                    UPDATE $tableName
                        SET newMaterial = e_newMaterial, newCategory = e_newCategory, timestamp = e_timestamp
                        WHERE world = e_world
                            AND context = e_context
                            AND blockKey = e_blockKey;
                ELSE
                    INSERT INTO $tableName
                        (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                        VALUES (e_context, e_type, e_world, e_blockKey, e_cause, e_causeName, e_x, e_y, e_z, e_material, e_blockData, e_category, e_newMaterial, e_newCategory, e_timestamp)
                        ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp);
                END IF;
                COMMIT;
            END;
        """.trimIndent())
    }

    // ReturnToWildPlugin
    suspend fun start() {
        dataSyncService.start()
    }
    suspend fun stop() {
        dataSyncService.stop()
    }

    // BlockChangeListener
    suspend fun saveQueued(change: BlockChange) = dataSyncService.write(SaveEntity(
        change.context, change.type, change.location.world.name, change.location.toBlockKey(),
        change.cause?.asString() ?: "minecraft:unknown", change.causeName ?: "unknown",
        change.location.blockX, change.location.blockY, change.location.blockZ, change.blockData.material.key.asString(),
        change.blockData.asString, change.category, change.newMaterial.key.asString(), change.newCategory, change.timestamp
    ))
    suspend fun saveAllQueued(changes: List<BlockChange>) {
        for (change in changes) {
            saveQueued(change)
        }
    }
    suspend fun saveWherePresentQueued(change: BlockChange, dependencyChange: DependencyChange) = dataSyncService.write(SaveConditionalEntity(
        change.type, change.location.world.name, change.location.toBlockKey(), change.location.blockX, change.location.blockY,
        change.location.blockZ, change.blockData.material.key.asString(), change.blockData.asString, change.category,
        change.newMaterial.key.asString(), change.newCategory, change.timestamp, dependencyChange.location.toBlockKey(),
        null, null, null, null, null, null, null, null, null
    ))
    suspend fun saveWhereOnePresentQueued(change: BlockChange, dependencyChanges: DependencyChanges) = dataSyncService.write(SaveConditionalEntity(
        change.type, change.location.world.name, change.location.toBlockKey(), change.location.blockX, change.location.blockY,
        change.location.blockZ, change.blockData.material.key.asString(), change.blockData.asString, change.category,
        change.newMaterial.key.asString(), change.newCategory, change.timestamp, dependencyChanges.locations[0].toBlockKey(),
        dependencyChanges.locations.getOrNull(1)?.toBlockKey(), dependencyChanges.locations.getOrNull(2)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(3)?.toBlockKey(), dependencyChanges.locations.getOrNull(4)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(5)?.toBlockKey(), dependencyChanges.locations.getOrNull(6)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(7)?.toBlockKey(), dependencyChanges.locations.getOrNull(8)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(9)?.toBlockKey()
    ))
    suspend fun saveAllWherePresentQueued(changes: List<BlockChange>, dependencyChange: DependencyChange) = changes
        .forEach { change-> saveWherePresentQueued(change, dependencyChange) }

    // ProgressiveRestorer, ClaimChangeListener
    suspend fun updateQueued(change: BlockChange, data: Map<String, Any>) {
        require(data.isNotEmpty())

        val newMaterial = data[UPDATE_NEW_MATERIAL] as Material? ?: change.newMaterial
        val newCategory = data[UPDATE_NEW_CATEGORY] as Int? ?: change.newCategory
        val timestamp = data[UPDATE_TIMESTAMP] as Instant? ?: change.timestamp

        dataSyncService.write(UpdateEntity(
            change.context, change.location.world.name, change.location.toBlockKey(), newMaterial.key.asString(), newCategory, timestamp
        ))
    }

    // ProgressiveRestorer
    suspend fun deleteQueued(change: BlockChange) {
        dataSyncService.write(DeleteEntity(
            change.context, change.location.world.name, change.location.toBlockKey()
        ))
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
            AND a.world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setString(2, cause.asString())
            statement.setTimestamp(3, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))
                if (world == null) {
                    null
                } else {
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
        }.filterNotNull()
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
            AND a.world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
        """.trimIndent(), { statement ->
            statement.setString(1, context)
            statement.setInt(2, newCategory)
            statement.setTimestamp(3, Timestamp.from(since))
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))
                if (world == null) {
                    null
                } else {
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
        }.filterNotNull()
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
            AND a.world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
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
                val world = server.getWorld(it.getString(5))
                if (world == null) {
                    null
                } else {
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
        }.filterNotNull()
    }

    // ClaimChangeListener
    suspend fun findByRegion(context: String, world: World, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): List<BlockChange> {
        return dataSource.query("""
            SELECT context, type, cause, causeName, world, x, y, z, blockData, category, newMaterial, newCategory, timestamp
            FROM $tableName
            WHERE context = ?
            AND world = ?
            AND x BETWEEN ? AND ?
            AND y BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
            AND world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
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

    // CommandService
    suspend fun findChanges(): List<BlockChange> {
        return dataSource.query("""
            SELECT context, type, cause, causeName, world, x, y, z, blockData, category, newMaterial, newCategory, timestamp
            FROM $tableName
        """.trimIndent()) { rs ->
            rs.map {
                val context = it.getString(1)
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))
                if (world == null) {
                    null
                } else {
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
        }.filterNotNull()
    }
    suspend fun findChanges(context: String): List<BlockChange> {
        return dataSource.query("""
            SELECT a.context, a.type, a.cause, a.causeName, a.world, a.x, a.y, a.z, a.blockData, a.category, a.newMaterial, a.newCategory, a.timestamp
            FROM (
                SELECT t.context, t.type, t.cause, t.causeName, t.world, t.x, t.y, t.z, t.blockData, t.category, t.newMaterial, t.newCategory, t.timestamp,
                    MAX(t.timestamp) OVER (PARTITION BY t.world, t.blockKey) AS maxTimestamp
                FROM $tableName AS t
            ) AS a
            WHERE a.maxTimestamp = a.timestamp
            AND a.context = ?
            AND a.world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
        """.trimIndent(), { statement ->
            statement.setString(1, context)
        }) { rs ->
            rs.map {
                val type = it.getString(2)
                val cause = it.getString(3)?.let { key -> NamespacedKey.fromString(key) }
                val causeName = it.getString(4)
                val world = server.getWorld(it.getString(5))
                if (world == null) {
                    null
                } else {
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
        }.filterNotNull()
    }
    suspend fun delete(blockChange: BlockChange): Boolean {
        val blockKey = blockChange.location.toBlockKey()
        return dataSource.update("""
            DELETE FROM $tableName
            WHERE context = ?
            AND world = ?
            AND blockKey = ?
        """.trimIndent()) { statement ->
            statement.setString(1, blockChange.context)
            statement.setString(2, blockChange.location.world.name)
            statement.setLong(3, blockKey)
        } == 1
    }
    suspend fun delete(context: String): Int {
        return dataSource.update("""
            DELETE FROM $tableName
            WHERE context = ?
            AND world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
        """.trimIndent()) { statement ->
            statement.setString(1, context)
        }
    }
    suspend fun deleteAll(): Int {
        return dataSource.update("""
            DELETE FROM $tableName
            WHERE world IN (${server.worlds.joinToString("','", "'", "'") { it.name }})
        """.trimIndent())
    }
}
