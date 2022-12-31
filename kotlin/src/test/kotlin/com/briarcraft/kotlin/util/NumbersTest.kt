package com.briarcraft.kotlin.util

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NumbersTest {
    @Nested
    inner class SumOfNullableTest {
        @Test
        fun `call with non-nulls`() {
            // Arrange
            val list = listOf(
                TestData(value = 1.0),
                TestData(value = 2.0),
                TestData(value = 3.0),
            )

            // Act
            val response = list.sumOfNullable(TestData::value)

            // Assert
            assertEquals(6.0, response)
        }

        @Test
        fun `call with nulls`() {
            // Arrange
            val list = listOf(
                TestData(value = 1.0),
                TestData(value = null),
                TestData(value = 3.0),
            )

            // Act
            val response = list.sumOfNullable(TestData::value)

            // Assert
            assertNull(response)
        }
    }

    data class TestData(
        val flag: Boolean? = null,
        val value: Double? = null,
    )
}