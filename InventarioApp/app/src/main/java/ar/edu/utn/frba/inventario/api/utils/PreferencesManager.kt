package ar.edu.utn.frba.inventario.api.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ar.edu.utn.frba.inventario.api.model.item.ItemStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveSelectedStatus(key: String, statusSet: Set<ItemStatus>) {
        val json = gson.toJson(statusSet.map { it.name })
        preferences.edit { putString(key, json) }
    }

    fun getSelectedStatus(key: String): Set<ItemStatus> {
        val json = preferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            val statusNames: List<String> = gson.fromJson(json, type) ?: emptyList()
            statusNames.mapNotNull { statusName ->
                try {
                    ItemStatus.valueOf(statusName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.toSet()
        } else {
            emptySet()
        }
    }

    fun clearSelectedStatus(key: String) {
        preferences.edit { remove(key) }
    }
}
