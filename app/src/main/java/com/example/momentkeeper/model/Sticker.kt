package com.example.momentkeeper.model

import androidx.compose.ui.geometry.Offset

data class Sticker(
    val id: String,
    val drawableResId: Int,
    val position: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val isSelected: Boolean = false
)
