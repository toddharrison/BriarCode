package com.briarcraft.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.viesoft.paperkit.core.plugin.KotlinPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.ServicePriority
import org.h2.tools.Server
import java.io.File

@Suppress("unused")
class DataSourcePlugin : KotlinPlugin() {

    private lateinit var dbConfigFile: File
    private lateinit var dataSource: HikariDataSource
    private var h2WebServer: Server? = null
    private var h2TcpServer: Server? = null

    override suspend fun loadConfig() {
        saveDefaultConfig()
        val dbConfigName = config.getString("datasource-config") ?: "datasource.properties"
        dbConfigFile = findConfigFileAndSaveIfNotExists(dbConfigName)
    }

    override suspend fun onEnabled() {
        withContext(Dispatchers.IO) {
            val isH2Enabled = config.getBoolean("enable-h2")
            if (isH2Enabled) {
                startH2()
            }

            dataSource = createDataSource()
            log.info { "Opened DataSource" }
        }

        server.servicesManager.register(
            DataSourceService::class.java,
            DataSourceService(this, dataSource),
            this,
            ServicePriority.Normal
        )
        log.info { "Registered DataSourceService" }
    }

    private fun startH2() {
        Class.forName("org.h2.Driver")
        h2WebServer = Server.createWebServer().start() // Allow access through web client
        h2TcpServer = Server.createTcpServer().start() // Allow access through DB client
        log.info { "Enabled H2 Server" }
    }

    override suspend fun onDisabled() {
        server.servicesManager.unregisterAll(this)
        log.info { "Unregistered services" }

        withContext(Dispatchers.IO) {
            h2WebServer?.stop()
            h2TcpServer?.stop()

            dataSource.close()
            log.info { "Closed data source" }
        }
    }

    private fun findConfigFileAndSaveIfNotExists(filename: String): File {
        return dataFolder.resolve(filename).apply {
            if (!exists()) saveResource(filename, false)
        }
    }

    private fun createDataSource() = HikariDataSource(HikariConfig(dbConfigFile.absolutePath))
}
