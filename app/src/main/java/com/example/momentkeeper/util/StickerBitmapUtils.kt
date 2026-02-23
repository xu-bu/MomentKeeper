package com.example.momentkeeper.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.ConcurrentHashMap

private val strippedCache = ConcurrentHashMap<Int, ImageBitmap>()

/**
 * 将 drawable 转为 Bitmap 并去除白色/浅灰（如 AI 生成的“透明”背景实为像素）为真正透明。
 * 在后台线程调用；结果按 resId 缓存。
 */
fun stripDrawableBackground(resId: Int, resources: android.content.res.Resources): ImageBitmap? {
    strippedCache[resId]?.let { return it }
    val drawable: Drawable = ResourcesCompat.getDrawable(resources, resId, null) ?: return null
    val w = drawable.intrinsicWidth.coerceAtLeast(1)
    val h = drawable.intrinsicHeight.coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, w, h)
    drawable.draw(canvas)

    val pixels = IntArray(w * h)
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

    for (i in pixels.indices) {
        val a = (pixels[i] shr 24) and 0xFF
        val r = (pixels[i] shr 16) and 0xFF
        val g = (pixels[i] shr 8) and 0xFF
        val b = pixels[i] and 0xFF
        if (isBackgroundPixel(a, r, g, b)) {
            pixels[i] = 0
        }
    }

    bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    val result = bitmap.asImageBitmap()
    strippedCache[resId] = result
    return result
}

/** 判断是否为“背景”像素：白色或中性浅灰（如 checkerboard），将改为透明 */
private fun isBackgroundPixel(a: Int, r: Int, g: Int, b: Int): Boolean {
    if (a < 128) return true
    val neutral = kotlin.math.abs(r - g) <= 25 && kotlin.math.abs(g - b) <= 25 && kotlin.math.abs(r - b) <= 25
    if (!neutral) return false
    val avg = (r + g + b) / 3
    return avg >= 200
}
