package com.briarcraft.kotlin.util

inline fun <T> Iterable<T?>.sumOfNullable(selector: (T) -> Double?): Double? {
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element == null) return null
        val add = selector(element) ?: return null
        sum += add
    }
    return sum
}

inline fun <T> Iterable<T?>.allNullable(predicate: (T) -> Boolean?): Boolean? {
    if (this is Collection && isEmpty()) return true
    for (element in this) {
        if (element == null) return null
        val result = predicate(element) ?: return null
        if (!result) return false
    }
    return true
}
