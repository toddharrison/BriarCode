package com.briarcraft.rtw.change.repo

interface QueuedRepositoryAction<T>

data class SaveItem<T>(
    val item: T
): QueuedRepositoryAction<T>

data class SaveItems<T>(
    val items: List<T>
): QueuedRepositoryAction<T>

data class SaveWherePresent<T>(
    val item: T,
    val change: DependencyChange
): QueuedRepositoryAction<T>

data class SaveAllWherePresent<T>(
    val items: List<T>,
    val change: DependencyChange
): QueuedRepositoryAction<T>

data class SaveWhereOnePresent<T>(
    val item: T,
    val changes: DependencyChanges
): QueuedRepositoryAction<T>

data class SaveAllWhereOnePresent<T>(
    val items: List<T>,
    val changes: DependencyChanges
): QueuedRepositoryAction<T>

data class UpdateItem<T>(
    val item: T,
    val data: Map<String, Any>
): QueuedRepositoryAction<T>

data class DeleteItem<T>(
    val item: T
): QueuedRepositoryAction<T>

data class DeleteItems<T>(
    val items: List<T>
): QueuedRepositoryAction<T>
