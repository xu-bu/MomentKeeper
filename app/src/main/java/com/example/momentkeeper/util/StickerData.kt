package com.example.momentkeeper.util

import com.example.momentkeeper.R

object StickerData {
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
