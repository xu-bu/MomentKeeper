package com.example.momentkeeper.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 可序列化的贴纸快照，用于保存/加载手帐。
 */
data class StickerSnapshot(
    val id: String,
    val drawableResId: Int? = null,
    val localUri: String? = null,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val text: String = "",
    val isTextSticker: Boolean = false,
    val baseSizeDp: Float = 120f,
    val fontSize: Float = 14f
) {
    fun toSticker(): Sticker = Sticker(
        id = id,
        drawableResId = drawableResId,
        localUri = localUri,
        position = Offset(positionX, positionY),
        scale = scale,
        rotation = rotation,
        text = text,
        isTextSticker = isTextSticker,
        baseSize = baseSizeDp.dp,
        fontSize = fontSize
    )

    companion object {
        fun fromSticker(s: Sticker): StickerSnapshot = StickerSnapshot(
            id = s.id,
            drawableResId = s.drawableResId,
            localUri = s.localUri,
            positionX = s.position.x,
            positionY = s.position.y,
            scale = s.scale,
            rotation = s.rotation,
            text = s.text,
            isTextSticker = s.isTextSticker,
            baseSizeDp = s.baseSize.value,
            fontSize = s.fontSize
        )
    }
}

/**
 * 手帐数据：唯一 id，多次保存同一手帐只更新不改动 id。
 */
data class Journal(
    val id: String,
    val title: String,
    val date: String,
    val eventDescription: String,
    val stickers: List<StickerSnapshot> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
)
