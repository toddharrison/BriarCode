package com.briarcraft.rtw.change.block

import java.time.Instant

interface ChangeEntity {
    val context: String?
    val type: String?
    val world: String
    val blockKey: Long
    val cause: String?
    val causeName: String?
    val x: Int?
    val y: Int?
    val z: Int?
    val material: String?
    val blockData: String?
    val category: Int?
    val newMaterial: String?
    val newCategory: Int?
    val timestamp: Instant?

    val blockKey1: Long?
    val blockKey2: Long?
    val blockKey3: Long?
    val blockKey4: Long?
    val blockKey5: Long?
    val blockKey6: Long?
    val blockKey7: Long?
    val blockKey8: Long?
    val blockKey9: Long?
    val blockKey10: Long?

    fun isValidContext() = context?.let { it.isNotEmpty() && it.length < 45 }
    fun isValidType() = type?.let { it.isNotEmpty() && it.length < 33 }
    fun isValidWorld() = world.let { it.isNotEmpty() && it.length < 33 }
    fun isValidCause() = cause?.let { it.isNotEmpty() && it.length < 65 }
    fun isValidCauseName() = causeName?.let { it.isNotEmpty() && it.length < 65 }
    fun isValidY() = y?.let { it >= -512 && it <= 511 }
    fun isValidMaterial() = material?.let { it.isNotEmpty() && it.length < 65 }
    fun isValidBlockData() = blockData?.let { it.isNotEmpty() && it.length < 257 }
    fun isValidNewMaterial() = newMaterial?.let { it.isNotEmpty() && it.length < 65 }
}

data class SaveEntity(
    override val context: String,
    override val type: String,
    override val world: String,
    override val blockKey: Long,
    override val cause: String,
    override val causeName: String,
    override val x: Int,
    override val y: Int,
    override val z: Int,
    override val material: String,
    override val blockData: String,
    override val category: Int,
    override val newMaterial: String,
    override val newCategory: Int,
    override val timestamp: Instant,
): ChangeEntity {
    override val blockKey1 = null
    override val blockKey2 = null
    override val blockKey3 = null
    override val blockKey4 = null
    override val blockKey5 = null
    override val blockKey6 = null
    override val blockKey7 = null
    override val blockKey8 = null
    override val blockKey9 = null
    override val blockKey10 = null

    init {
        require(isValidContext() == true) { "save context invalid '$context'" }
        require(isValidType() == true) { "save type invalid '$type'" }
        require(isValidWorld()) { "save world invalid '$world'" }
        require(isValidCause() == true) { "save cause invalid '$cause'" }
        require(isValidCauseName() == true) { "save causeName invalid '$causeName'" }
        require(isValidY() == true) { "save y invalid '$y'" }
        require(isValidMaterial() == true) { "save material invalid '$material'" }
        require(isValidBlockData() == true) { "save blockData invalid '$blockData'" }
        require(isValidNewMaterial() == true) { "save newMaterial invalid '$newMaterial'" }
    }
}

data class SaveConditionalEntity(
    override val type: String,
    override val world: String,
    override val blockKey: Long,
    override val x: Int,
    override val y: Int,
    override val z: Int,
    override val material: String,
    override val blockData: String,
    override val category: Int,
    override val newMaterial: String,
    override val newCategory: Int,
    override val timestamp: Instant,

    override val blockKey1: Long,
    override val blockKey2: Long?,
    override val blockKey3: Long?,
    override val blockKey4: Long?,
    override val blockKey5: Long?,
    override val blockKey6: Long?,
    override val blockKey7: Long?,
    override val blockKey8: Long?,
    override val blockKey9: Long?,
    override val blockKey10: Long?,
): ChangeEntity {
    override val context = null
    override val cause = null
    override val causeName = null

    init {
        require(isValidType() == true) { "saveConditional type invalid '$type'" }
        require(isValidWorld()) { "saveConditional world invalid '$world'" }
        require(isValidY() == true) { "saveConditional y invalid '$y'" }
        require(isValidMaterial() == true) { "saveConditional material invalid '$material'" }
        require(isValidBlockData() == true) { "saveConditional blockData invalid '$blockData'" }
        require(isValidNewMaterial() == true) { "saveConditional newMaterial invalid '$newMaterial'" }
    }
}

data class UpdateEntity(
    override val context: String,
    override val world: String,
    override val blockKey: Long,
    override val newMaterial: String,
    override val newCategory: Int,
    override val timestamp: Instant,
): ChangeEntity {
    override val type = null
    override val cause = null
    override val causeName = null
    override val x = null
    override val y = null
    override val z = null
    override val material = null
    override val blockData = null
    override val category = null

    override val blockKey1 = null
    override val blockKey2 = null
    override val blockKey3 = null
    override val blockKey4 = null
    override val blockKey5 = null
    override val blockKey6 = null
    override val blockKey7 = null
    override val blockKey8 = null
    override val blockKey9 = null
    override val blockKey10 = null

    init {
        require(isValidContext() == true) { "update context invalid '$context'" }
        require(isValidWorld()) { "update world invalid '$world'" }
        require(isValidNewMaterial() == true) { "update newMaterial invalid '$newMaterial'" }
    }
}

data class DeleteEntity(
    override val context: String,
    override val world: String,
    override val blockKey: Long,
): ChangeEntity {
    override val type = null
    override val cause = null
    override val causeName = null
    override val x = null
    override val y = null
    override val z = null
    override val material = null
    override val blockData = null
    override val category = null
    override val newMaterial = null
    override val newCategory = null
    override val timestamp = null

    override val blockKey1 = null
    override val blockKey2 = null
    override val blockKey3 = null
    override val blockKey4 = null
    override val blockKey5 = null
    override val blockKey6 = null
    override val blockKey7 = null
    override val blockKey8 = null
    override val blockKey9 = null
    override val blockKey10 = null

    init {
        require(isValidContext() == true) { "delete context invalid '$context'" }
        require(isValidWorld()) { "delete world invalid '$world'" }
    }
}
