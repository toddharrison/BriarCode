package com.briarcraft.kotlin.util

import java.util.*

inline fun <T: Enum<T>> EnumSet<T>.subSet(crossinline action: (T) -> Boolean): EnumSet<T> =
    clone().also { it.removeIf { t -> !action(t) } }

fun <T: Enum<T>> EnumSet<T>.union(set: Set<T>): EnumSet<T> =
    clone().also { it.addAll(set) }

inline fun <reified T: Enum<T>> EnumSet<T>.intersection(set: Set<T>): EnumSet<T> =
    clone().also { it.removeIf { element -> !set.contains(element) }}

inline fun <reified T: Enum<T>> EnumSet<T>.invert(): EnumSet<T> =
    EnumSet.allOf(T::class.java).also { it.removeAll(this) }



inline fun <reified T: Enum<T>> Sequence<T>.toEnumSet(): EnumSet<T> =
    EnumSet.noneOf(T::class.java).also { it.addAll(this) }

inline fun <reified T: Enum<T>> Iterable<T>.toEnumSet(): EnumSet<T> =
    EnumSet.noneOf(T::class.java).also { it.addAll(this) }

inline fun <reified T: Enum<T>> Set<T>.toEnumSet(): EnumSet<T> =
    EnumSet.copyOf(this)

inline fun <reified T: Enum<T>> enumSetOf(vararg values: T): EnumSet<T> =
    EnumSet.noneOf(T::class.java).also { it.addAll(values) }



inline fun <reified T: Enum<T>> Iterable<T>.asEnumSet(): EnumSet<T> =
    if (this is EnumSet) this else toEnumSet()

inline fun <reified T: Enum<T>> Set<T>.asEnumSet(): EnumSet<T> =
    if (this is EnumSet) this else toEnumSet()



inline fun <reified T: Enum<T>, U> Sequence<Pair<T, U>>.toEnumMap(): EnumMap<T, U> =
    EnumMap<T, U>(T::class.java).also { it.putAll(this) }

inline fun <reified T: Enum<T>, U> Iterable<Pair<T, U>>.toEnumMap(): EnumMap<T, U> =
    EnumMap<T, U>(T::class.java).also { it.putAll(this) }

inline fun <reified T: Enum<T>, U> Map<T, U>.toEnumMap(): EnumMap<T, U> =
    EnumMap<T, U>(this)

inline fun <reified T: Enum<T>, U> enumMapOf(vararg pairs: Pair<T, U>): EnumMap<T, U> =
    EnumMap<T, U>(T::class.java).also { it.putAll(pairs) }



inline fun <reified T: Enum<T>, U> Map<T, U>.asEnumMap(): EnumMap<T, U> =
    if (this is EnumMap) this else toEnumMap()



operator fun <T: Enum<T>> EnumSet<T>.plus(set: EnumSet<T>): EnumSet<T> =
    clone().also { it.addAll(set) }

operator fun <T: Enum<T>> EnumSet<T>.minus(set: EnumSet<T>): EnumSet<T> =
    clone().also { it.removeAll(set) }
