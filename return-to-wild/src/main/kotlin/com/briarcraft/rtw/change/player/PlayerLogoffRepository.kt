package com.briarcraft.rtw.change.player

import com.briarcraft.datasource.DataSourceService
import com.briarcraft.rtw.repo.Repository
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import java.sql.Timestamp
import java.util.UUID

class PlayerLogoffRepository(
    override val server: Server,
    override val dataSource: DataSourceService,
    override val tableName: String = "rtw_player_logoff_2"
): Repository {
    override suspend fun createTable() {
        dataSource.execute("""
            CREATE TABLE IF NOT EXISTS $tableName (
                name VARCHAR(32) NOT NULL,
                id CHAR(36) NOT NULL,
                world VARCHAR(32) NOT NULL,
                x INT NOT NULL,
                y SMALLINT NOT NULL,
                z INT NOT NULL,
                timestamp TIMESTAMP NOT NULL,
                PRIMARY KEY (id)
            )""".trimIndent())
    }

    suspend fun save(player: PlayerLogoff) {
        dataSource.execute("""
            INSERT INTO $tableName
            (name, id, world, x, y, z, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE name = VALUES(name), world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z), timestamp = VALUES(timestamp)            
        """.trimIndent()) { statement ->
            statement.setString(1, player.name)
            statement.setString(2, player.id.toString())
            statement.setString(3, player.location.world.name)
            statement.setInt(4, player.location.blockX)
            statement.setInt(5, player.location.blockY)
            statement.setInt(6, player.location.blockZ)
            statement.setTimestamp(7, Timestamp.from(player.timestamp))
        }
    }

    suspend fun findById(id: UUID): PlayerLogoff? {
        return dataSource.query("""
            SELECT name, id, world, x, y, z, timestamp FROM $tableName
            WHERE id = ?
        """.trimIndent(), { statement ->
            statement.setString(1, id.toString())
        }) { rs ->
            if (rs.next()) {
                val name = rs.getString(1)
                val world = Bukkit.getWorld(rs.getString(3))!!
                val x = rs.getInt(4).toDouble()
                val y = rs.getInt(5).toDouble()
                val z = rs.getInt(6).toDouble()
                val location = Location(world, x, y, z)
                val timestamp = rs.getTimestamp(7).toInstant()

                PlayerLogoff(name, id, location, timestamp)
            } else null
        }
    }
}
