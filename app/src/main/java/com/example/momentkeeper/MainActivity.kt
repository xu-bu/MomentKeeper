package com.example.momentkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.momentkeeper.model.Sticker
import com.example.momentkeeper.ui.canvas.CanvasView
import com.example.momentkeeper.ui.sidebar.InfoSidebar
import com.example.momentkeeper.ui.sticker.StickerContainer
import com.example.momentkeeper.util.StickerData
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MomentKeeperApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentKeeperApp() {
    // UI state
    var isTopStickerVisible by remember { mutableStateOf(false) }
    var stickers by remember { mutableStateOf(StickerData.defaultStickers) }
    var selectedStickerId by remember { mutableStateOf<String?>(null) }
    
    // Drag and drop state
    var draggingStickerResId by remember { mutableStateOf<Int?>(null) }
    var draggingPosition by remember { mutableStateOf(Offset.Zero) }
    var rootBoxPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { InfoSidebar() }
        }
    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .onGloballyPositioned { layoutCoordinates ->
                        rootBoxPositionInWindow = layoutCoordinates.positionInWindow()
                    }
            ) {
                // 1. The Canvas (Always in the background)
                CanvasView(
                    stickers = stickers,
                    onUpdateSticker = { updatedSticker ->
                        stickers = stickers.map { 
                            if (it.id == updatedSticker.id) updatedSticker else it 
                        }
                    },
                    onCanvasClick = {
                        scope.launch { drawerState.open() }
                    },
                    onSelectSticker = { id ->
                        selectedStickerId = id
                        stickers = stickers.map { 
                            it.copy(isSelected = it.id == id)
                        }
                    },
                    onDeleteSticker = { stickerId ->
                        stickers = stickers.filterNot { it.id == stickerId }
                    }
                )

                // 2. The Add Button
                Button(
                    onClick = { isTopStickerVisible = !isTopStickerVisible },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    Text(if (isTopStickerVisible) "Close Stickers" else "Add Sticker")
                }

                // 3. The Sticker Picker
                if (isTopStickerVisible) {
                    StickerContainer(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp),
                        onStickerSelected = { resId ->
                            val newSticker = Sticker(
                                id = UUID.randomUUID().toString(),
                                drawableResId = resId,
                                position = Offset(200f, 400f)
                            )
                            stickers = stickers + newSticker
                            isTopStickerVisible = false
                        },
                        onDragStart = { resId, posInWindow ->
                            draggingStickerResId = resId
                            draggingPosition = posInWindow
                        },
                        onDrag = { delta ->
                            draggingPosition += delta
                        },
                        onDragEnd = {
                            draggingStickerResId?.let { resId ->
                                val stickerSizePx = with(density) { 60.dp.toPx() } // Half of 120dp
                                
                                // Calculate position relative to the rootBox
                                val dropLocalPos = draggingPosition - rootBoxPositionInWindow - Offset(stickerSizePx, stickerSizePx)
                                
                                val newSticker = Sticker(
                                    id = UUID.randomUUID().toString(),
                                    drawableResId = resId,
                                    position = dropLocalPos
                                )
                                stickers = stickers + newSticker
                                isTopStickerVisible = false
                            }
                            draggingStickerResId = null
                        },
                        draggingResId = draggingStickerResId // Pass the current dragging ID
                    )
                }

                // 4. Dragging Overlay (Visual feedback while dragging)
                // This must be at the end to stay on top of everything.
                draggingStickerResId?.let { resId ->
                    val stickerSize = 120.dp
                    val stickerSizePx = with(density) { (stickerSize / 2).toPx() }
                    
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(stickerSize)
                            .offset { 
                                IntOffset(
                                    (draggingPosition.x - rootBoxPositionInWindow.x - stickerSizePx).roundToInt(),
                                    (draggingPosition.y - rootBoxPositionInWindow.y - stickerSizePx).roundToInt()
                                ) 
                            }
                            .graphicsLayer(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
