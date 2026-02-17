package com.example.momentkeeper.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Sticker(
    val id: String,
    val drawableResId: Int? = null, // 内置贴纸资源 ID，变为可选
    val localUri: String? = null,   // 新增：用户上传贴纸的本地路径或 URI
    val position: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val isSelected: Boolean = false,
    val isEditing: Boolean = false,
    val text: String = "",
    val isTextSticker: Boolean = false,
    val baseSize: Dp = 120.dp,
    val fontSize: Float = 14f
) {
    // 辅助属性：返回图片的来源（可以是 Int 或 String/Uri）
    val imageSource: Any?
        get() = localUri ?: drawableResId
}
