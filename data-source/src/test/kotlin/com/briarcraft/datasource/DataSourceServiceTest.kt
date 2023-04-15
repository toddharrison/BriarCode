package com.briarcraft.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.bukkit.block.Block
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.sql.Timestamp
import java.time.Instant
import java.util.Random
import java.util.logging.Logger
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

@Disabled
@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceServiceTest {
    companion object {
        private val logger: Logger = Logger.getLogger("Test Logger")

        private lateinit var dataSource: HikariDataSource
        private lateinit var service: DataSourceService

        @JvmStatic
        @BeforeAll
        fun openConnection() {
            dataSource = createDataSource()
            service = DataSourceService(logger, dataSource)
        }

        @JvmStatic
        @AfterAll
        fun closeConnection() {
            dataSource.close()
        }

        private fun createDataSource(): HikariDataSource {
            val resourceUrl = DataSourceServiceTest::class.java.getResource("/test.properties")!!
            val resourceUri = resourceUrl.toURI()
            val resourceFile = File(resourceUri)
            val resourcePath = resourceFile.absolutePath
            return HikariDataSource(HikariConfig(resourcePath))
        }
    }

    private val tableName = "todd"
    private val worldName = "world"
    private val ran = Random()
    private val blockY = 0

    @Test
    fun `blockKey ranges`() {
        assertEquals(0, Block.getBlockKey(0, 0, 0))
        assertEquals(-1, Block.getBlockKey(-1, -1, -1))
        println(Block.getBlockKeyX(-1))
        println(Block.getBlockKeyY(-1))
        println(Block.getBlockKeyZ(-1))
    }

    @Test
    fun `call with two queries`() = runTest(dispatchTimeoutMs = 120000) {
        createStoredProcedure()

        println("ms to delete all: " + measureTimeMillis {
            clearTable()
            // 312
        })

        println("ms to insert one: " + measureTimeMillis {
            service.update("""
                INSERT INTO $tableName
                    (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()) { statement ->
                statement.setString(1, "test")
                statement.setString(2, "type")
                statement.setString(3, worldName)
                statement.setLong(4, -1)
                statement.setString(5, "minecraft:test")
                statement.setString(6, "call")
                statement.setInt(7, -1)
                statement.setInt(8, blockY)
                statement.setInt(9, -1)
                statement.setString(10, "material:-1")
                statement.setString(11, "blockData")
                statement.setInt(12, -1)
                statement.setString(13, "new-material:-1")
                statement.setInt(14, 0)
                statement.setTimestamp(15, Timestamp.from(Instant.now()))
            }
            // 454
        })

        val maxX = 100
        val maxZ = 10

        val blockChanges = (1..maxX).flatMap { blockX ->
            (1..maxZ).map { blockZ ->
                BlockChange("type", worldName, (blockX + blockZ * (maxX * maxZ)).toLong(), blockX, blockY, blockZ, "material:$blockX", "blockData", blockX, "new-material:$blockZ", blockZ, Instant.now())
            }
        }

        val elapsed = measureTimeMillis {
            val changes = (1..10).map { ran.nextInt() } + listOf(-1)

//            blockChanges.forEach {
                // 2 Queries
//                val latestChange = findLatestChanges(worldName, changes)!!
//                val recordsWritten = saveAllWhereOnePresent(listOf(it), latestChange)
//                // 456

                // 1 Query
//                val recordsWritten = saveAllWhereLatestChangePresent(listOf(it), changes)
//                // 390

//                println(recordsWritten.size)
//            }

            val recordsWritten = saveAllWhereLatestChangePresent(blockChanges, changes)
            // 69
            println(recordsWritten.size)
        }
        println("ms per record: ${elapsed.toDouble() / (maxX * maxZ)}")
    }

    private suspend fun createStoredProcedure() {
        service.execute("""
            CREATE OR REPLACE PROCEDURE rtw_test(
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
                        VALUES (e_context, e_type, e_worldName, e_blockKey, e_cause, e_causeName, e_blockX, e_blockY, e_blockZ, e_material, e_blockData, e_category, e_newMaterial, e_newCategory, e_timestamp)
                        ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
                ELSE
                    INSERT INTO $tableName
                        (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                        (SELECT t2.context, e_type, t2.world, e_blockKey, t2.cause, t2.causeName, e_blockX, e_blockY, e_blockZ, e_material, e_blockData, e_category, e_newMaterial, e_newCategory, e_timestamp
                            FROM $tableName AS t2
                            WHERE world=e_worldName
                                AND blockKey IN (e_blockKey_1, e_blockKey_2, e_blockKey_3, e_blockKey_4, e_blockKey_5, e_blockKey_6, e_blockKey_7, e_blockKey_8, e_blockKey_9, e_blockKey_10)
                            ORDER BY timestamp DESC
                            LIMIT 1
                        )
                        ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp);
                END IF;
                COMMIT;
            END;
        """.trimIndent())
    }

    private suspend fun clearTable() {
        println("Deleted " + service.update("DELETE FROM $tableName WHERE TRUE") + " records")
    }

    private suspend fun findLatestChanges(worldName: String, changes: List<Int>): LatestChange? {
        if (changes.isEmpty()) return null
        require(changes.isNotEmpty())
        return service.statement(String.format("""
            SELECT context, cause, causeName FROM $tableName
            WHERE world='$worldName'
            AND blockKey IN (%s)
            ORDER BY timestamp DESC
            LIMIT 1
        """.trimIndent(), changes.joinToString(","))) { rs ->
            if (rs.next()) LatestChange(rs.getString(1), rs.getString(2), rs.getString(3)) else null
        }
    }

    private suspend fun saveAllWhereOnePresent(items: List<BlockChange>, latestChange: LatestChange) =
        service.batch("""
            INSERT INTO $tableName
                (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp)
        """.trimIndent(), items, false) { statement, item ->
            statement.setString(1, latestChange.context)
            statement.setString(2, item.type)
            statement.setString(3, item.worldName)
            statement.setLong(4, item.blockKey)
            statement.setString(5, latestChange.cause)
            statement.setString(6, latestChange.causeName)
            statement.setInt(7, item.blockX)
            statement.setInt(8, item.blockY)
            statement.setInt(9, item.blockZ)
            statement.setString(10, item.material)
            statement.setString(11, item.blockData)
            statement.setInt(12, item.category)
            statement.setString(13, item.newMaterial)
            statement.setInt(14, item.newCategory)
            statement.setTimestamp(15, Timestamp.from(item.timestamp))
        }

    private suspend fun saveAllWhereLatestChangePresent(items: List<BlockChange>, changes: List<Int>) =
        service.batch(String.format("""
            INSERT INTO $tableName
                (context, type, world, blockKey, cause, causeName, x, y, z, material, blockData, category, newMaterial, newCategory, timestamp)
                (SELECT t2.context, ?, t2.world, ?, t2.cause, t2.causeName, ?, ?, ?, ?, ?, ?, ?, ?, ?
                    FROM $tableName AS t2
                    WHERE world=?
                        AND blockKey IN (%s)
                    ORDER BY timestamp DESC
                    LIMIT 1
                )
                ON DUPLICATE KEY UPDATE newMaterial = VALUES(newMaterial), newCategory = VALUES(newCategory), timestamp = VALUES(timestamp);
        """.trimIndent(), changes.joinToString(",")), items, false) { statement, item ->
            statement.setString(1, item.type)
            statement.setLong(2, item.blockKey)
            statement.setInt(3, item.blockX)
            statement.setInt(4, item.blockY)
            statement.setInt(5, item.blockZ)
            statement.setString(6, item.material)
            statement.setString(7, item.blockData)
            statement.setInt(8, item.category)
            statement.setString(9, item.newMaterial)
            statement.setInt(10, item.newCategory)
            statement.setTimestamp(11, Timestamp.from(item.timestamp))
            statement.setString(12, item.worldName)
        }

    data class LatestChange(
        val context: String,
        val cause: String,
        val causeName: String
    )

    data class BlockChange(
        val type: String,
        val worldName: String,
        val blockKey: Long,
        val blockX: Int,
        val blockY: Int,
        val blockZ: Int,
        val material: String,
        val blockData: String,
        val category: Int,
        val newMaterial: String,
        val newCategory: Int,
        val timestamp: Instant
    )
}
