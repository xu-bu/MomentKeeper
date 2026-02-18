package com.example.momentkeeper.ui.sticker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.momentkeeper.util.StickerData

@Composable
fun StickerContainer(
    modifier: Modifier = Modifier,
    onStickerSelected: (Int) -> Unit,
    onUploadClick: () -> Unit, // 新增：点击上传按钮的回调
    customStickerPaths: List<String> = emptyList(),
    onCustomStickerSelected: (String) -> Unit = {},
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
            .height(245.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 默认内置贴纸区域（仅内置贴纸，可拖拽）
            val defaultChunks = StickerData.availableStickerResIds.chunked(3)
            defaultChunks.forEach { rowIds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowIds.forEach { resId ->
                        // 普通内置贴纸
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
                            AsyncImage(
                                model = resId,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // 自定义上传贴纸区域：包含“添加”按钮和用户贴纸
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "My Stickers",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val myItems: List<String?> = listOf<String?>(null) + customStickerPaths
            val customChunks = myItems.chunked(3)
            customChunks.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    rowItems.forEach { pathOrNull ->
                        if (pathOrNull == null) {
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
                            // 用户自定义贴纸（点击添加到画布，不支持拖拽）
                            val path = pathOrNull
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { onCustomStickerSelected(path) },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = path,
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
