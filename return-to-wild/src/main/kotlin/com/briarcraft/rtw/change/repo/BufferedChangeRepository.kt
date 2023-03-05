package com.briarcraft.rtw.change.repo

import java.util.*
import java.util.logging.Logger

abstract class BufferedChangeRepository<T>: ChangeRepository<T> {
    private val actions: Queue<QueuedRepositoryAction<T>> = LinkedList()

    fun countQueuedActions() = actions.size

    suspend fun executeNext(count: Int) {
        repeat(count) {
            when (val action = actions.poll()) {
                null -> return
                is SaveItem<T> -> save(action.item)
                is SaveItems<T> -> saveAll(action.items)
                is SaveWherePresent<T> -> saveWherePresent(action.item, action.change)
                is SaveAllWherePresent<T> -> saveAllWherePresent(action.items, action.change)
                is SaveWhereOnePresent<T> -> saveWhereOnePresent(action.item, action.changes)
                is SaveAllWhereOnePresent<T> -> saveAllWhereOnePresent(action.items, action.changes)
                is UpdateItem<T> -> update(action.item, action.data)
                is DeleteItem<T> -> delete(action.item)
                is DeleteItems<T> -> deleteAll(action.items)
            }
        }
    }

    suspend fun executeAll(log: Logger) {
        log.info("Rows remaining: ${actions.size}")
        do {
            val action = actions.poll()
            when (action) {
                is SaveItem<T> -> save(action.item)
                is SaveItems<T> -> saveAll(action.items)
                is SaveWherePresent<T> -> saveWherePresent(action.item, action.change)
                is SaveAllWherePresent<T> -> saveAllWherePresent(action.items, action.change)
                is SaveWhereOnePresent<T> -> saveWhereOnePresent(action.item, action.changes)
                is SaveAllWhereOnePresent<T> -> saveAllWhereOnePresent(action.items, action.changes)
                is UpdateItem<T> -> update(action.item, action.data)
                is DeleteItem<T> -> delete(action.item)
                is DeleteItems<T> -> deleteAll(action.items)
            }
        } while (action != null)
        log.info("Completed saving")
    }

    fun saveQueued(item: T) = actions.add(SaveItem(item))
    fun saveAllQueued(items: List<T>) = actions.add(SaveItems(items))
    fun saveWherePresentQueued(item: T, change: DependencyChange) = actions.add(SaveWherePresent(item, change))
    fun saveAllWherePresentQueued(items: List<T>, change: DependencyChange) = actions.add(
        SaveAllWherePresent(
            items,
            change
        )
    )
    fun saveWhereOnePresentQueued(item: T, changes: DependencyChanges) = actions.add(SaveWhereOnePresent(item, changes))
    fun saveAllWhereOnePresentQueued(items: List<T>, changes: DependencyChanges) = actions.add(
        SaveAllWhereOnePresent(
            items,
            changes
        )
    )

    fun updateQueued(item: T, data: Map<String, Any>) = actions.add(UpdateItem(item, data))

    fun deleteQueued(item: T) = actions.add(DeleteItem(item))
    fun deleteAllQueued(items: List<T>) = actions.add(DeleteItems(items))
}
