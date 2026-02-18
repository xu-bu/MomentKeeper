package com.example.momentkeeper.data

import android.content.Context
import com.example.momentkeeper.model.Journal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "moment_keeper_journals"
private const val KEY_JOURNALS = "saved_journals"

/**
 * 手帐持久化：使用 SharedPreferences + JSON。
 * 同一 id 多次保存会覆盖，保证一个手帐只存一条。
 */
class JournalRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<Journal>>() {}.type

    fun getAll(): List<Journal> {
        val json = prefs.getString(KEY_JOURNALS, null) ?: return emptyList()
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** 保存或更新手帐（同一 id 只保留一条，按 updatedAt 更新） */
    fun save(journal: Journal) {
        val list = getAll().toMutableList()
        val index = list.indexOfFirst { it.id == journal.id }
        val updated = journal.copy(updatedAt = System.currentTimeMillis())
        if (index >= 0) {
            list[index] = updated
        } else {
            list.add(updated)
        }
        prefs.edit().putString(KEY_JOURNALS, gson.toJson(list)).apply()
    }

    fun getById(id: String): Journal? = getAll().find { it.id == id }

    /** 删除指定 id 的手帐 */
    fun delete(id: String) {
        val list = getAll().filter { it.id != id }
        prefs.edit().putString(KEY_JOURNALS, gson.toJson(list)).apply()
    }
}
