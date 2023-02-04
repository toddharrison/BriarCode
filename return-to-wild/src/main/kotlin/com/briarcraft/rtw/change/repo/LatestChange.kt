package com.briarcraft.rtw.change.repo

data class LatestChange(
    val context: String,
    val cause: String?,
    val causeName: String?
)
