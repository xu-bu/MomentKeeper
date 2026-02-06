package com.example.momentkeeper.ui.canvas

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.momentkeeper.model.Sticker
import kotlin.math.roundToInt

@Composable
fun StickerView(
    sticker: Sticker,
    onUpdateSticker: (Sticker) -> Unit,
    onSelect: () -> Unit
) {
    // Use rememberUpdatedState to ensure the gesture detector always uses the latest sticker data
    // without restarting the pointerInput coroutine, which would break the ongoing gesture.
    val currentSticker by rememberUpdatedState(sticker)

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    currentSticker.position.x.roundToInt(),
                    currentSticker.position.y.roundToInt()
                )
            }
            .pointerInput(sticker.id) {
                detectTapGestures(onTap = { onSelect() })
            }
            .pointerInput(sticker.id) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    onUpdateSticker(
                        currentSticker.copy(
                            position = currentSticker.position + pan,
                            scale = (currentSticker.scale * zoom).coerceIn(0.5f, 5f),
                            rotation = currentSticker.rotation + rotation
                        )
                    )
                }
            }
            .graphicsLayer(
                scaleX = currentSticker.scale,
                scaleY = currentSticker.scale,
                rotationZ = currentSticker.rotation
            )
            .then(
                if (currentSticker.isSelected) {
                    Modifier.border(2.dp, Color(0xFF6200EE))
                } else {
                    Modifier
                }
            )
    ) {
        Image(
            painter = painterResource(id = currentSticker.drawableResId),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
    }
}
