package com.briarcraft.datasource

import java.sql.ResultSet

fun ResultSet.getNullableByte(columnIndex: Int) = getByte(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableShort(columnIndex: Int) = getShort(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableInt(columnIndex: Int) = getInt(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableLong(columnIndex: Int) = getLong(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableFloat(columnIndex: Int) = getFloat(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableDouble(columnIndex: Int) = getDouble(columnIndex).let { if (wasNull()) null else it }
fun ResultSet.getNullableBoolean(columnIndex: Int) = getBoolean(columnIndex).let { if (wasNull()) null else it }

fun ResultSet.getNullableByte(columnLabel: String) = getByte(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableShort(columnLabel: String) = getShort(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableInt(columnLabel: String) = getInt(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableLong(columnLabel: String) = getLong(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableFloat(columnLabel: String) = getFloat(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableDouble(columnLabel: String) = getDouble(columnLabel).let { if (wasNull()) null else it }
fun ResultSet.getNullableBoolean(columnLabel: String) = getBoolean(columnLabel).let { if (wasNull()) null else it }
