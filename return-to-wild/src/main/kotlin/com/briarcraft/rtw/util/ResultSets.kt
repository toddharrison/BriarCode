package com.briarcraft.rtw.util

import java.sql.ResultSet

inline fun <T> ResultSet.map(transform: (ResultSet) -> T) =
    use {
        val results = mutableListOf<T>()
        while (it.next()) results.add(transform(it))
        results
    }
