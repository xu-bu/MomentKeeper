package com.example.momentkeeper.ui.sticker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.momentkeeper.util.StickerData

@Composable
fun StickerContainer(
    modifier: Modifier = Modifier,
    onStickerSelected: (Int) -> Unit,
    onDragStart: (Int, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    draggingResId: Int? = null
) {
    val currentOnStickerSelected by rememberUpdatedState(onStickerSelected)
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)

    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val chunks = StickerData.availableStickerResIds.chunked(3)
            chunks.forEach { rowResIds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowResIds.forEach { resId ->
                        var itemPositionInWindow by remember { mutableStateOf(Offset.Zero) }
                        val isDraggingThis = draggingResId == resId
                        
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(80.dp)
                                .onGloballyPositioned { layoutCoordinates ->
                                    itemPositionInWindow = layoutCoordinates.positionInWindow()
                                }
                                .alpha(if (isDraggingThis) 0.3f else 1.0f)
                                .pointerInput(resId) {
                                    // 使用 detectDragGestures 代替长按触发，提高灵敏度
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentOnDragStart(resId, itemPositionInWindow + offset)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            currentOnDrag(dragAmount)
                                        },
                                        onDragEnd = { currentOnDragEnd() },
                                        onDragCancel = { currentOnDragEnd() }
                                    )
                                }
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
