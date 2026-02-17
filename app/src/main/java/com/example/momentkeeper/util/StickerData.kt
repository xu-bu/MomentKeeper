package com.example.momentkeeper.util

import com.example.momentkeeper.R
import com.example.momentkeeper.model.Sticker
import java.util.UUID

object StickerData {
    val defaultStickers = listOf(
        Sticker(
            id = UUID.randomUUID().toString(),
            drawableResId = R.drawable.ic_sticker_diary_lines,
            isTextSticker = true,
            text = "在这里写下点什么吧！"
        )
    )
    
    val availableStickerResIds = listOf(
        R.drawable.ic_sticker_diary_lines,
        R.drawable.ic_sticker_heart,
        R.drawable.ic_sticker_star,
        R.drawable.ic_sticker_sun,
        R.drawable.ic_launcher_foreground
    )

    // 辅助方法：判断是否为文字贴纸
    fun isTextSticker(resId: Int): Boolean {
        return resId == R.drawable.ic_sticker_diary_lines
    }
}
