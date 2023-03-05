package com.briarcraft.datasource

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.ServicePriority
import org.h2.tools.Server
import java.io.File

@Suppress("unused")
class DataSourcePlugin: SuspendingJavaPlugin() {
    private lateinit var dbConfigFile: File
    private lateinit var dataSource: HikariDataSource
    private var h2WebServer: Server? = null
    private var h2TcpServer: Server? = null

    override suspend fun onLoadAsync() {
        saveDefaultConfig()
        val dbConfigName = config.getString("datasource-config") ?: "datasource.properties"
        dbConfigFile = findConfigFileAndSaveIfNotExists(dbConfigName)
        logger.info { "Loaded DataSource config" }
    }

    override suspend fun onEnableAsync() {
        withContext(Dispatchers.IO) {
            val isH2Enabled = config.getBoolean("h2.enable")
            if (isH2Enabled) {
                Class.forName("org.h2.Driver")
                logger.info { "Enabled H2 local database" }

                val isH2WebEnabled = config.getBoolean("h2.web.enable")
                if (isH2WebEnabled) {
                    h2WebServer = Server.createWebServer().start() // Allow access through web client
                    logger.warning { "Enabled H2 web Server, allowing external web access!" }
                }

                val isH2TcpEnabled = config.getBoolean("h2.tcp.enable")
                if (isH2TcpEnabled) {
                    h2TcpServer = Server.createTcpServer().start() // Allow access through DB client
                    logger.warning { "Enabled H2 TCP Server, allowing external client access!" }
                }
            }

            dataSource = createDataSource()
            logger.info { "Opened DataSource" }
        }

        server.servicesManager.register(
            DataSourceService::class.java,
            DataSourceService(logger, dataSource),
            this,
            ServicePriority.Normal)
        logger.info { "Registered DataSourceService" }
    }

    override suspend fun onDisableAsync() {
        server.servicesManager.unregisterAll(this)
        logger.info("Unregistered services")

        withContext(Dispatchers.IO) {
            h2WebServer?.stop()
            h2TcpServer?.stop()

            dataSource.close()
            logger.info { "Closed data source" }
        }
    }

    private fun findConfigFileAndSaveIfNotExists(filename: String): File {
        return dataFolder.resolve(filename).apply {
            if (!exists()) saveResource(filename, false)
        }
    }

    private fun createDataSource() = HikariDataSource(HikariConfig(dbConfigFile.absolutePath))
}
