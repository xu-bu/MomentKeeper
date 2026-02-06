package com.example.momentkeeper.util

import com.example.momentkeeper.R
import com.example.momentkeeper.model.Sticker
import java.util.UUID

object StickerData {
    val defaultStickers = listOf(
        Sticker(
            id = UUID.randomUUID().toString(),
            drawableResId = R.drawable.ic_launcher_foreground // Using launcher icon as placeholder
        )
    )
    
    // In a real app, this would return a list of drawable resources for the container
    val availableStickerResIds = listOf(
        R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_background
    )
}
