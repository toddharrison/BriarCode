package com.briarcraft.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.sql.Types
import java.util.logging.Logger
import kotlin.test.*

// https://phauer.com/2018/best-practices-unit-testing-kotlin/
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceFunctionalTest {
    private val logger = Logger.getLogger("Test Logger")
    private val dataSource: HikariDataSource
    private val dataSourceService: DataSourceService

    init {
        Class.forName("org.h2.Driver")
        val resourceUrl = DataSourceFunctionalTest::class.java.getResource("/test.properties")!!
        val resourcePath = File(resourceUrl.toURI()).absolutePath
        dataSource = HikariDataSource(HikariConfig(resourcePath))
        dataSourceService = DataSourceService(logger, dataSource)
    }

    @Nested
    inner class DataCacheServiceTest {
        @field:TempDir lateinit var cacheDir: File

        private lateinit var dataCacheService: DataCacheService<TestData>

        @BeforeTest
        fun createTestTable(): Unit = runBlocking {
            dataSourceService.execute("CREATE TABLE IF NOT EXISTS TEST (a VARCHAR(32) NULL, b INT NULL, c BIGINT NULL, d BOOLEAN NULL)")
        }

        @BeforeEach
        fun createDataCacheService() = runBlocking {
            dataSourceService.execute("DELETE FROM TEST")
            dataCacheService = DataCacheService(dataSourceService,
                "INSERT INTO TEST (a, b, c, d) VALUES (?, ?, ?, ?)",
                cacheDir,
                "test",
                "tsv",
                { sequenceOf(it.a, it.b, it.c, it.d) }
            ) { statement, data ->
                statement.setObject(1, data[0], Types.VARCHAR)
                statement.setObject(2, data[1], Types.INTEGER)
                statement.setObject(3, data[2], Types.BIGINT)
                statement.setObject(4, data[3], Types.BOOLEAN)
            }
        }

        @Nested
        inner class WriteToCacheTest {
            @Test
            fun `cache data records to file`() = runTest {
                // Arrange
                val data = TestData("1", 2, 3L, true)

                // Act
                val openCacheFile = dataCacheService.openCache()
                val write = dataCacheService.writeToCache(data)
                val itemCount = dataCacheService.getItemsInCache()
                val closeCacheFile = dataCacheService.closeCache()

                // Assert
                assertTrue { write }
                assertEquals(1, itemCount)
                assertEquals(0, dataCacheService.getItemsInCache())
                assertTrue { openCacheFile == closeCacheFile }
                assertTrue { closeCacheFile?.exists() ?: false }

                // Cleanup
                closeCacheFile?.delete()
            }

            @Test
            fun `cache data records with null values to file`() = runTest {
                // Arrange
                val data = TestData(null, null, null, null)

                // Act
                val openCacheFile = dataCacheService.openCache()
                val write = dataCacheService.writeToCache(data)
                val itemCount = dataCacheService.getItemsInCache()
                val closeCacheFile = dataCacheService.closeCache()

                // Assert
                assertTrue { write }
                assertEquals(1, itemCount)
                assertEquals(0, dataCacheService.getItemsInCache())
                assertTrue { openCacheFile == closeCacheFile }
                assertTrue { closeCacheFile?.exists() ?: false }

                closeCacheFile?.forEachLine { println(it) }

                // Cleanup
                closeCacheFile?.delete()
            }
        }

        @Nested
        inner class WriteCacheToDatabaseTest {
            @Test
            fun `call with no cache file available`() = runTest {
                // Arrange

                // Act
                val writtenCacheFile = dataCacheService.writeCacheToDatabase()

                // Assert
                assertEquals(0, dataCacheService.getItemsInCache())
                assertNull(writtenCacheFile)
            }

            @Test
            fun `call with cache file available but not closed`() = runTest {
                // Arrange
                val data = TestData("1", 2, 3L, true)

                // Act
                val openCacheFile = dataCacheService.openCache()
                val write = dataCacheService.writeToCache(data)
                val itemCount = dataCacheService.getItemsInCache()
                val writtenCacheFile = dataCacheService.writeCacheToDatabase()
                val closeCacheFile = dataCacheService.closeCache()

                // Assert
                assertTrue { write }
                assertEquals(1, itemCount)
                assertEquals(0, dataCacheService.getItemsInCache())
                assertEquals(openCacheFile, closeCacheFile)
                assertNull(writtenCacheFile)

                // Cleanup
                closeCacheFile?.delete()
            }

            @Test
            fun `call with cache file available`() = runTest {
                // Arrange
                val data1 = TestData("1", 2, 3L, true)
                val data2 = TestData("2", 3, 4L, false)
                val data3 = TestData("3", 4, 5L, true)

                // Act
                val openCacheFile = dataCacheService.openCache()
                val write = dataCacheService.writeToCache(listOf(data1, data2, data3))
                val itemCount = dataCacheService.getItemsInCache()
                val closeCacheFile = dataCacheService.closeCache()

                val writtenCacheFile = dataCacheService.writeCacheToDatabase()

                // Assert
                assertTrue { write }
                assertEquals(3, itemCount)
                assertEquals(0, dataCacheService.getItemsInCache())
                assertEquals(openCacheFile, closeCacheFile)
                assertEquals(openCacheFile, writtenCacheFile)
                assertFalse { writtenCacheFile?.exists() ?: false }

                dataSourceService.query("SELECT a, b, c, d FROM TEST") { rs ->
                    assertTrue(rs.next())
                    assertEquals(
                        data1,
                        TestData(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getBoolean(4)))

                    assertTrue(rs.next())
                    assertEquals(
                        data2,
                        TestData(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getBoolean(4)))

                    assertTrue(rs.next())
                    assertEquals(
                        data3,
                        TestData(rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getBoolean(4)))

                    assertFalse(rs.next())
                }
            }

            @Test
            fun `call with null values in data`() = runTest {
                // Arrange
                val data1 = TestData(null, 2, 3L, true)
                val data2 = TestData("2", null, 4L, false)
                val data3 = TestData("3", 4, null, true)
                val data4 = TestData("4", 5, 6L, null)

                // Act
                val openCacheFile = dataCacheService.openCache()
                val write = dataCacheService.writeToCache(listOf(data1, data2, data3, data4))
                val itemCount = dataCacheService.getItemsInCache()
                val closeCacheFile = dataCacheService.closeCache()

                val writtenCacheFile = dataCacheService.writeCacheToDatabase()

                // Assert
                assertTrue { write }
                assertEquals(4, itemCount)
                assertEquals(0, dataCacheService.getItemsInCache())
                assertEquals(openCacheFile, closeCacheFile)
                assertEquals(openCacheFile, writtenCacheFile)
                assertFalse { writtenCacheFile?.exists() ?: false }

                dataSourceService.query("SELECT a, b, c, d FROM TEST") { rs ->
                    assertTrue(rs.next())
                    assertEquals(
                        data1,
                        TestData(rs.getString(1), rs.getNullableInt(2), rs.getNullableLong(3), rs.getNullableBoolean(4)))

                    assertTrue(rs.next())
                    assertEquals(
                        data2,
                        TestData(rs.getString(1), rs.getNullableInt(2), rs.getNullableLong(3), rs.getNullableBoolean(4)))

                    assertTrue(rs.next())
                    assertEquals(
                        data3,
                        TestData(rs.getString(1), rs.getNullableInt(2), rs.getNullableLong(3), rs.getNullableBoolean(4)))

                    assertTrue(rs.next())
                    assertEquals(
                        data4,
                        TestData(rs.getString(1), rs.getNullableInt(2), rs.getNullableLong(3), rs.getNullableBoolean(4)))

                    assertFalse(rs.next())
                }
            }
        }
    }

    data class TestData(
        val a: String?,
        val b: Int?,
        val c: Long?,
        val d: Boolean?
    )
}
