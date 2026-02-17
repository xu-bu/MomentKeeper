package com.example.momentkeeper.ui.sticker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
    onUploadClick: () -> Unit, // 新增：点击上传按钮的回调
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
            // 将上传按钮放在列表的最前面
            val allItems = listOf(-1) + StickerData.availableStickerResIds // -1 代表上传按钮
            
            val chunks = allItems.chunked(3)
            chunks.forEach { rowIds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowIds.forEach { id ->
                        if (id == -1) {
                            // 上传按钮
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { onUploadClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Upload Sticker",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            // 普通贴纸
                            val resId = id
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
}
