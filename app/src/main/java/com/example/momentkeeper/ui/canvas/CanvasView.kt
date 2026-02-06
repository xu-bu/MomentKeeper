package com.example.momentkeeper.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.momentkeeper.model.Sticker

@Composable
fun CanvasView(
    stickers: List<Sticker>,
    onUpdateSticker: (Sticker) -> Unit,
    onCanvasClick: () -> Unit,
    onSelectSticker: (String?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .clickable { 
                onSelectSticker(null) // Deselect all stickers when clicking canvas
                onCanvasClick() 
            }
    ) {
        stickers.forEach { sticker ->
            key(sticker.id) {
                StickerView(
                    sticker = sticker,
                    onUpdateSticker = onUpdateSticker,
                    onSelect = { onSelectSticker(sticker.id) }
                )
            }
        }
    }
}
