package com.example.momentkeeper.util

import com.example.momentkeeper.R

object StickerData {
    /*
     * 自动从 R.drawable 中收集所有贴纸资源。
     * 约定：仅包含名称以 "sticker_" 或 "ic_sticker_" 开头的 drawable。
     * 新增贴纸只需在 res/drawable 放入对应命名文件，无需改代码。
     */
    val availableStickerResIds: List<Int> by lazy {
        R.drawable::class.java.declaredFields
            .filter { field ->
                field.type == Int::class.javaPrimitiveType &&
                    (field.name.startsWith("sticker_"))
            }
            .mapNotNull { field ->
                try {
                    field.isAccessible = true
                    field.getInt(null)
                } catch (e: Exception) {
                    null
                }
            }
    }

    // 辅助方法：判断是否为文字贴纸
    fun isTextSticker(resId: Int): Boolean {
        return resId == R.drawable.sticker_diary_lines
    }
}
