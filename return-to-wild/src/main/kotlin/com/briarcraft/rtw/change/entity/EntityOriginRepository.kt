package com.briarcraft.rtw.change.entity

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.repo.Repository
import com.briarcraft.rtw.util.map
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import java.sql.Timestamp

class EntityOriginRepository(
    override val server: Server,
    override val dataSource: DataSourceService,
    override val tableName: String = "rtw_entity_origin"
): Repository {
    override suspend fun createTable() {
        dataSource.execute("""
            CREATE TABLE IF NOT EXISTS $tableName (
                type VARCHAR(32) NOT NULL,
                world VARCHAR(32) NOT NULL,
                x INT NOT NULL,
                y SMALLINT NOT NULL,
                z INT NOT NULL,
                facing VARCHAR(16) NOT NULL,
                passengers VARCHAR(256) NULL,
                loot VARCHAR(42) NULL,
                detail VARCHAR(256) NULL,
                timestamp TIMESTAMP NOT NULL
            )""".trimIndent())
    }

    suspend fun save(entity: EntityOrigin) {
        dataSource.execute("""
                INSERT INTO $tableName
                (type, world, x, y, z, facing, passengers, loot, detail, timestamp)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()) { statement ->
            statement.setString(1, entity.type.key.asString())
            statement.setString(2, entity.location.world.name)
            statement.setInt(3, entity.location.blockX)
            statement.setInt(4, entity.location.blockY)
            statement.setInt(5, entity.location.blockZ)
            statement.setString(6, entity.facing.name)
            statement.setString(7, if (entity.passengers.isNullOrEmpty()) null else entity.passengers.map { it.type }.joinToString(","))
            statement.setString(8, entity.loot?.asString())
            statement.setString(9, entity.detail)
            statement.setTimestamp(10, Timestamp.from(entity.timestamp))
        }
    }

    suspend fun find(type: EntityType, location: Location, range: Int): List<EntityOrigin> {
        require(range >= 0)

        val world = location.world
        val minX = location.blockX - range
        val maxX = location.blockX + range
        val minY = location.blockY - range
        val maxY = location.blockY + range
        val minZ = location.blockZ - range
        val maxZ = location.blockZ + range

        return dataSource.query("""
            SELECT x, y, z, facing, passengers, loot, detail
            FROM $tableName
            WHERE type = ?
            AND world = ?
            AND x BETWEEN ? AND ?
            AND y BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
        """.trimIndent(), { statement ->
            statement.setString(1, type.key.asString())
            statement.setString(2, world.name)
            statement.setInt(3, minX)
            statement.setInt(4, maxX)
            statement.setInt(5, minY)
            statement.setInt(6, maxY)
            statement.setInt(7, minZ)
            statement.setInt(8, maxZ)
        }) { rs ->
            rs.map {
                val x = it.getInt(1).toDouble() + 0.5
                val y = it.getInt(2).toDouble()
                val z = it.getInt(3).toDouble() + 0.5
                val facing = BlockFace.valueOf(it.getString(4))
                val passengers = null // it.getString(5)
                val loot = it.getString(6)?.let { key -> NamespacedKey.fromString(key) }
                val detail = it.getString(7)
                EntityOrigin(type, Location(world, x, y, z), facing, passengers, loot, detail)
            }
        }
    }
}
