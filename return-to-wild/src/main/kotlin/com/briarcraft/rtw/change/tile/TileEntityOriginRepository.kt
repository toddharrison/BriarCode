package com.briarcraft.rtw.change.tile

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.kotlin.util.ChunkLocation
import com.briarcraft.rtw.repo.Repository
import com.briarcraft.rtw.util.map
import org.bukkit.*
import java.sql.Timestamp

class TileEntityOriginRepository(
    override val server: Server,
    override val dataSource: DataSourceService,
    override val tableName: String = "rtw_tile_entity_origin"
): Repository {
    override suspend fun createTable() {
        dataSource.execute("""
            CREATE TABLE IF NOT EXISTS $tableName (
                type VARCHAR(32) NOT NULL,
                world VARCHAR(32) NOT NULL,
                x INT NOT NULL,
                y SMALLINT NOT NULL,
                z INT NOT NULL,
                data VARCHAR(256) NOT NULL,
                detail VARCHAR(256) NULL,
                timestamp TIMESTAMP NOT NULL
            )""".trimIndent())
    }

    suspend fun save(tileEntity: TileEntityOrigin) {
        dataSource.execute("""
            INSERT INTO $tableName
            (type, world, x, y, z, data, detail, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()) { statement ->
            statement.setString(1, tileEntity.type.key.asString())
            statement.setString(2, tileEntity.location.world.name)
            statement.setInt(3, tileEntity.location.blockX)
            statement.setInt(4, tileEntity.location.blockY)
            statement.setInt(5, tileEntity.location.blockZ)
            statement.setString(6, tileEntity.data)
            statement.setString(7, tileEntity.detail)
            statement.setTimestamp(8, Timestamp.from(tileEntity.timestamp))
        }
    }

    suspend fun find(type: Material, chunk: Chunk): List<TileEntityOrigin> {
        val world = chunk.world
        val loc = ChunkLocation(chunk.x, chunk.z)
        val min = loc.toMinBlockLocation()
        val max = loc.toMaxBlockLocation()

        return dataSource.query("""
            SELECT x, y, z, data, detail
            FROM  $tableName
            WHERE type = ?
            AND world = ?
            AND x BETWEEN ? AND ?
            AND y BETWEEN ? AND ?
            AND z BETWEEN ? AND ?
        """.trimIndent(), { statement ->
            statement.setString(1, type.key.asString())
            statement.setString(2, world.name)
            statement.setInt(3, min.x)
            statement.setInt(4, max.x)
            statement.setInt(5, min.y)
            statement.setInt(6, max.y)
            statement.setInt(7, min.z)
            statement.setInt(8, max.z)
        }) { rs ->
            rs.map {
                val x = it.getInt(1).toDouble()
                val y = it.getInt(2).toDouble()
                val z = it.getInt(3).toDouble()
                val data = it.getString(4)
                val detail = it.getString(5)
                TileEntityOrigin(type, Location(world, x, y, z), data, detail)
            }
        }
    }
}
