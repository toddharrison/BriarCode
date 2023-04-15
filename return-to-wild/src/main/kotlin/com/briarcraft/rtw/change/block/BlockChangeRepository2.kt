package com.briarcraft.rtw.change.block

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.change.repo.DependencyChange
import com.briarcraft.rtw.change.repo.DependencyChanges
import com.briarcraft.rtw.repo.Repository
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.World
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import java.util.*
import java.util.logging.Logger
import kotlin.math.min

class BlockChangeRepository2(
    override val server: Server,
    override val dataSource: DataSourceService,
    override val tableName: String = "rtw_block_change_2"
): Repository {
    private val actions: Queue<ChangeEntity> = LinkedList()

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
    suspend fun executeNext(log: Logger, count: Int) {
        if (count > 0 && actions.size > 0) {
            val size = actions.size
            saveAll(generateSequence { actions.poll() }.take(count))
            log.info("Saved changes: ${min(count, size)} / $size")
        }
    }
    suspend fun executeAll(log: Logger) {
        val size = actions.size
        saveAll(generateSequence { actions.poll() })
        log.info("Saved changes: $size")
    }

    // BlockChangeListener
    fun saveQueued(change: BlockChange) = actions.add(SaveEntity(
        change.context, change.type, change.location.world.name, change.location.toBlockKey(),
        change.cause?.asString() ?: "minecraft:unknown", change.causeName ?: "unknown",
        change.location.blockX, change.location.blockY, change.location.blockZ, change.blockData.material.key.asString(),
        change.blockData.asString, change.category, change.newMaterial.key.asString(), change.newCategory, change.timestamp
    ))
    fun saveAllQueued(changes: List<BlockChange>) = changes.forEach(::saveQueued)
    fun saveWherePresentQueued(change: BlockChange, dependencyChange: DependencyChange) = actions.add(SaveConditionalEntity(
        change.type, change.location.world.name, change.location.toBlockKey(), change.location.blockX, change.location.blockY,
        change.location.blockZ, change.blockData.material.key.asString(), change.blockData.asString, change.category,
        change.newMaterial.key.asString(), change.newCategory, change.timestamp, dependencyChange.location.toBlockKey(),
        null, null, null, null, null, null, null, null, null
    ))
    fun saveWhereOnePresentQueued(change: BlockChange, dependencyChanges: DependencyChanges) = actions.add(SaveConditionalEntity(
        change.type, change.location.world.name, change.location.toBlockKey(), change.location.blockX, change.location.blockY,
        change.location.blockZ, change.blockData.material.key.asString(), change.blockData.asString, change.category,
        change.newMaterial.key.asString(), change.newCategory, change.timestamp, dependencyChanges.locations[0].toBlockKey(),
        dependencyChanges.locations.getOrNull(1)?.toBlockKey(), dependencyChanges.locations.getOrNull(2)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(3)?.toBlockKey(), dependencyChanges.locations.getOrNull(4)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(5)?.toBlockKey(), dependencyChanges.locations.getOrNull(6)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(7)?.toBlockKey(), dependencyChanges.locations.getOrNull(8)?.toBlockKey(),
        dependencyChanges.locations.getOrNull(9)?.toBlockKey()
    ))
    fun saveAllWherePresentQueued(changes: List<BlockChange>, dependencyChange: DependencyChange) = changes
        .forEach { change-> saveWherePresentQueued(change, dependencyChange) }

    // ProgressiveRestorer, ClaimChangeListener
    fun updateQueued(change: BlockChange, data: Map<String, Any>): Boolean {
        require(data.isNotEmpty())

        val newMaterial = data[UPDATE_NEW_MATERIAL] as Material? ?: change.newMaterial
        val newCategory = data[UPDATE_NEW_CATEGORY] as Int? ?: change.newCategory
        val timestamp = data[UPDATE_TIMESTAMP] as Instant? ?: change.timestamp

        return actions.add(UpdateEntity(
            change.context, change.location.world.name, change.location.toBlockKey(), newMaterial.key.asString(), newCategory, timestamp
        ))
    }

    // ProgressiveRestorer
    fun deleteQueued(change: BlockChange) = actions.add(DeleteEntity(
        change.context, change.location.world.name, change.location.toBlockKey()
    ))
    suspend fun findChanges(context: String, cause: NamespacedKey, since: Instant): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }
    suspend fun findByNewCategory(context: String, newCategory: Int, since: Instant): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }
    suspend fun findByCategories(context: String, category: Int, newCategory: Int, since: Instant): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }

    // ClaimChangeListener
    suspend fun findByRegion(context: String, world: World, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }

    // CommandService
    suspend fun findChanges(): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }
    suspend fun findChanges(context: String): List<BlockChange> {
        check(actions.isEmpty())
        // TODO
        return listOf()
    }
    suspend fun delete(blockChange: BlockChange) {
        check(actions.isEmpty())
        // TODO
    }
    suspend fun delete(context: String) {
        check(actions.isEmpty())
        // TODO
    }
    suspend fun deleteAll() {
        check(actions.isEmpty())
        // TODO
    }



    private suspend fun saveAll(entities: Sequence<ChangeEntity>) {
        dataSource.batchCall("""
            CALL ${tableName}_change(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent(), entities) { statement, entity ->
            statement.setString(1, entity.context)
            statement.setString(2, entity.type)
            statement.setString(3, entity.world)
            statement.setLong(4, entity.blockKey)
            statement.setString(5, entity.cause)
            statement.setString(6, entity.causeName)
            statement.setObject(7, entity.x, Types.INTEGER)
            statement.setObject(8, entity.y, Types.SMALLINT)
            statement.setObject(9, entity.z, Types.INTEGER)
            statement.setString(10, entity.material)
            statement.setString(11, entity.blockData)
            statement.setObject(12, entity.category, Types.INTEGER)
            statement.setString(13, entity.newMaterial)
            statement.setObject(14, entity.newCategory, Types.INTEGER)
            statement.setTimestamp(15, Timestamp.from(entity.timestamp))
            statement.setObject(16, entity.blockKey1, Types.BIGINT)
            statement.setObject(17, entity.blockKey2, Types.BIGINT)
            statement.setObject(18, entity.blockKey3, Types.BIGINT)
            statement.setObject(19, entity.blockKey4, Types.BIGINT)
            statement.setObject(20, entity.blockKey5, Types.BIGINT)
            statement.setObject(21, entity.blockKey6, Types.BIGINT)
            statement.setObject(22, entity.blockKey7, Types.BIGINT)
            statement.setObject(23, entity.blockKey8, Types.BIGINT)
            statement.setObject(24, entity.blockKey9, Types.BIGINT)
            statement.setObject(25, entity.blockKey10, Types.BIGINT)
        }
    }
}
