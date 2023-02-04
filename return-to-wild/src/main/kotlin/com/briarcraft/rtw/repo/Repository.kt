package com.briarcraft.rtw.repo

import com.briarcraft.datasource.DataSourceService
import org.bukkit.Server

interface Repository {
    val server: Server
    val dataSource: DataSourceService
    val tableName: String

    suspend fun createTable()
}
