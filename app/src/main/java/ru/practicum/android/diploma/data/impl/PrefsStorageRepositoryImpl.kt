package ru.practicum.android.diploma.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.domain.models.FilterSettings
import ru.practicum.android.diploma.domain.repository.PrefsStorageRepository
import java.lang.reflect.Type

class PrefsStorageRepositoryImpl(
    private val prefs: SharedPreferences,
    private val gson: Gson,
) : PrefsStorageRepository{

    override fun storeFilterSettings(data: FilterSettings) {
        storeData(data, FILTER_SETTINGS_DATA_KEY)
    }

    override fun getFilterSettings(): FilterSettings? {
        return getData(object : TypeToken<FilterSettings>() {}.type, FILTER_SETTINGS_DATA_KEY)
    }

    fun <T> storeData(data: T, dataKey: String) {
        prefs.edit {
            putString(
                dataKey,
                gson.toJson(data)
            )
        }
    }

    fun <T> getData(type: Type, dataKey: String): T? {
        val dataJson = prefs.getString(dataKey, null)
        return dataJson?.let { gson.fromJson(dataJson, type) }
    }

    companion object {
        private const val FILTER_SETTINGS_DATA_KEY = "filter_settings"
    }

}
