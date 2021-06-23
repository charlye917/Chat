package com.charlye934.chat.models

import java.util.*

data class Rates(
    val userId: String = "",
    val text: String = "",
    val rate: Float = 0f,
    val createdAt: Date = Date(),
    val profileImgURL: String = ""
)