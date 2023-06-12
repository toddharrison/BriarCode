package com.briarcraft.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.sql.Types
import java.util.logging.Logger
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.seconds

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
class DataSynchronizationServiceFunctionalTest {
    private val logger = Logger.getLogger("Test Logger")
    private val dataSource: HikariDataSource
    private val dataSourceService: DataSourceService

    init {
        val resourceUrl = DataSourceFunctionalTest::class.java.getResource("/test.properties")!!
        val resourcePath = File(resourceUrl.toURI()).absolutePath
        dataSource = HikariDataSource(HikariConfig(resourcePath))
        dataSourceService = DataSourceService(logger, dataSource)
    }

    @Nested
    inner class DataCacheServiceTest {
        @field:TempDir lateinit var cacheDir: File

        private lateinit var dataCacheService: DataCacheService<TestData>
        private lateinit var service: DataSynchronizationService<TestData>

        @BeforeTest
        fun createTestTable(): Unit = runBlocking {
            dataSourceService.execute("CREATE TABLE IF NOT EXISTS TEST (a VARCHAR(32))")
        }

        @BeforeEach
        fun createDataCacheService() = runBlocking {
            dataSourceService.execute("DELETE FROM TEST")
            dataCacheService = DataCacheService(dataSourceService,
                "INSERT INTO TEST (a) VALUES (?)",
                cacheDir,
                "test",
                "tsv",
                { sequenceOf(it.a) }
            ) { statement, data ->
                statement.setObject(1, data[0], Types.VARCHAR)
            }
            service = DataSynchronizationService(logger, dataCacheService, 3)
        }

        @Nested
        inner class WriteStreamTest {
            @Test
            fun `do something`() = runTest() {
                // Arrange
                val data = sequenceOf(
                    TestData("2"),
                    TestData("3"),
                    TestData("4"),
                    TestData("5"),
                    TestData("6"),
                    TestData("7"),
                    TestData("8"),
                    TestData(null),
                )

                // Act
                service.start()
                service.write(TestData("1"))
                service.write(data)
                withContext(Dispatchers.Default) { delay(10.seconds) }
                service.write(TestData("10"))
                service.stop()

                // Assert
            }
        }
    }

    data class TestData(
        val a: String?,
    )
}
