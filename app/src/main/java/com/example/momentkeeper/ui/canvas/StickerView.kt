package com.example.momentkeeper.ui.canvas

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.momentkeeper.model.Sticker
import kotlin.math.roundToInt

@Composable
fun StickerView(
    sticker: Sticker,
    onUpdateSticker: (Sticker) -> Unit,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val currentSticker by rememberUpdatedState(sticker)
    var showMenu by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }

    val currentSize = currentSticker.baseSize * currentSticker.scale

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    currentSticker.position.x.roundToInt(),
                    currentSticker.position.y.roundToInt()
                )
            }
            .size(currentSize)
            .pointerInput(sticker.id) {
                detectTapGestures(
                    onTap = { onSelect() },
                    onLongPress = {
                        onSelect()
                        showMenu = true
                    }
                )
            }
            .pointerInput(sticker.id) {
                if (!isEditing) {
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
            }
            .graphicsLayer(
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
        // 1. Background Image
        Image(
            painter = painterResource(id = currentSticker.drawableResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Text Content
        if (currentSticker.isTextSticker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp * currentSticker.scale),
                contentAlignment = Alignment.Center
            ) {
                // Font size is now fixed (or only changed via menu)
                val fontSize = currentSticker.fontSize.sp
                
                if (isEditing) {
                    BasicTextField(
                        value = currentSticker.text,
                        onValueChange = { newText ->
                            onUpdateSticker(currentSticker.copy(text = newText))
                        },
                        textStyle = TextStyle(
                            fontSize = fontSize,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = currentSticker.text,
                        fontSize = fontSize,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Context Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            if (currentSticker.isTextSticker) {
                DropdownMenuItem(
                    text = { Text(if (isEditing) "Finish Editing" else "Edit Text") },
                    onClick = {
                        isEditing = !isEditing
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Change Font Size") },
                    onClick = {
                        showFontSizeDialog = true
                        showMenu = false
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onDelete()
                    showMenu = false
                }
            )
        }
    }

    // Font Size Selection Dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text("Select Font Size") },
            text = {
                Column {
                    listOf(12f, 14f, 18f, 22f, 26f, 30f).forEach { size ->
                        TextButton(
                            onClick = {
                                onUpdateSticker(currentSticker.copy(fontSize = size))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${size.toInt()} sp", fontSize = size.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { }) {
                    Text("Cancel")
                }
            }
        )
    }
}
