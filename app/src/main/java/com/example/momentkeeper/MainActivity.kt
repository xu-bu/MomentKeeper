package com.example.momentkeeper

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.momentkeeper.model.Sticker
import com.example.momentkeeper.ui.canvas.CanvasView
import com.example.momentkeeper.ui.sidebar.InfoSidebar
import com.example.momentkeeper.ui.sticker.StickerContainer
import com.example.momentkeeper.util.StickerData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // UI state
    var isTopStickerVisible by remember { mutableStateOf(false) }
    var stickers by remember { mutableStateOf(emptyList<Sticker>()) }
    var customStickerPaths by remember { mutableStateOf(emptyList<String>()) }

    // Drag and drop state
    var draggingStickerResId by remember { mutableStateOf<Int?>(null) }
    var draggingPosition by remember { mutableStateOf(Offset.Zero) }
    var rootBoxPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val density = LocalDensity.current

    // 加载已保存的自定义贴纸文件列表
    LaunchedEffect(context) {
        val directory = File(context.filesDir, "custom_stickers")
        if (directory.exists()) {
            val files = directory.listFiles()
            if (files != null) {
                customStickerPaths = files
                    .filter { it.isFile }
                    .map { it.absolutePath }
            }
        }
    }

    // 图片选择器启动器
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val localPath = saveImageToInternalStorage(context, uri)
                if (localPath != null) {
                    val newId = UUID.randomUUID().toString()
                    val newSticker = Sticker(
                        id = newId,
                        localUri = localPath,
                        position = Offset(200f, 400f),
                        isSelected = true
                    )
                    // 将用户上传的贴纸路径加入可选贴纸列表中
                    customStickerPaths = (customStickerPaths + localPath).distinct()
                    stickers = stickers.map { it.copy(isSelected = false, isEditing = false) } + newSticker
                    isTopStickerVisible = false
                }
            }
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { InfoSidebar() }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Button(
                            onClick = { isTopStickerVisible = !isTopStickerVisible }
                        ) {
                            Text(if (isTopStickerVisible) "Close Stickers" else "Add Sticker")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Sidebar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .onGloballyPositioned { layoutCoordinates ->
                        rootBoxPositionInWindow = layoutCoordinates.positionInWindow()
                    }
            ) {
                // 1. The Canvas
                CanvasView(
                    stickers = stickers,
                    onUpdateSticker = { updatedSticker ->
                        stickers = stickers.map { 
                            if (it.id == updatedSticker.id) updatedSticker else it 
                        }
                    },
                    onCanvasClick = {
                        isTopStickerVisible = false
                    },
                    onSelectSticker = { id ->
                        stickers = stickers.map { 
                            it.copy(
                                isSelected = it.id == id,
                                isEditing = if (it.id != id) false else it.isEditing
                            )
                        }
                    },
                    onDeleteSticker = { stickerId ->
                        stickers = stickers.filterNot { it.id == stickerId }
                    }
                )

                // 2. The Sticker Picker
                if (isTopStickerVisible) {
                    StickerContainer(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp),
                        customStickerPaths = customStickerPaths,
                        onStickerSelected = { resId ->
                            val isText = StickerData.isTextSticker(resId)
                            val newId = UUID.randomUUID().toString()
                            val newSticker = Sticker(
                                id = newId,
                                drawableResId = resId,
                                position = Offset(200f, 400f),
                                isTextSticker = isText,
                                text = if (isText) "在这里写下点什么吧！" else "",
                                isSelected = true,
                                isEditing = isText
                            )
                            stickers = stickers.map { it.copy(isSelected = false, isEditing = false) } + newSticker
                            isTopStickerVisible = false
                        },
                        onCustomStickerSelected = { path ->
                            val newId = UUID.randomUUID().toString()
                            val newSticker = Sticker(
                                id = newId,
                                localUri = path,
                                position = Offset(200f, 400f),
                                isSelected = true
                            )
                            stickers = stickers.map { it.copy(isSelected = false, isEditing = false) } + newSticker
                            isTopStickerVisible = false
                        },
                        onUploadClick = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                                val stickerSizePx = with(density) { 60.dp.toPx() }
                                val dropLocalPos = draggingPosition - rootBoxPositionInWindow - Offset(stickerSizePx, stickerSizePx)
                                
                                val isText = StickerData.isTextSticker(resId)
                                val newId = UUID.randomUUID().toString()
                                val newSticker = Sticker(
                                    id = newId,
                                    drawableResId = resId,
                                    position = dropLocalPos,
                                    isTextSticker = isText,
                                    text = if (isText) "在这里写下点什么吧！" else "",
                                    isSelected = true,
                                    isEditing = isText
                                )
                                stickers = stickers.map { it.copy(isSelected = false, isEditing = false) } + newSticker
                                isTopStickerVisible = false
                            }
                            draggingStickerResId = null
                        },
                        draggingResId = draggingStickerResId
                    )
                }

                // 3. Dragging Overlay
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

/**
 * 将用户选择的图片保存到应用的内部存储目录，并返回本地路径。
 */
suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
        val directory = File(context.filesDir, "custom_stickers")
        if (!directory.exists()) directory.mkdirs()
        
        val fileName = "sticker_${UUID.randomUUID()}.png"
        val file = File(directory, fileName)
        
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
