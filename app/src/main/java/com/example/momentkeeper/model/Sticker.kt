package com.example.momentkeeper.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sticker(
    val id: String,
    val drawableResId: Int,
    val position: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val isSelected: Boolean = false,
    val text: String = "",
    val isTextSticker: Boolean = false,
    val baseSize: Dp = 120.dp,
    val fontSize: Float = 14f // Add fontSize, default to 14f
)
