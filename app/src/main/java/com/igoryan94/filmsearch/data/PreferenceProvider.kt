package com.igoryan94.filmsearch.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceProvider(val context: Context) {
    // Нам нужен контекст приложения
    private val appContext = context.applicationContext

    // Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences =
        appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        // Логика для первого запуска приложения, чтобы положить наши настройки,
        // Сюда потом можно добавить и другие настройки
        if (preference.getBoolean(KEY_FIRST_LAUNCH, false)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    // Category prefs
    // Сохраняем категорию
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }

    // Забираем категорию
    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    // Etc.
    fun saveLastCacheRefreshTime(time: Long) {
        preference.edit { putLong(KEY_LAST_CACHE_REFRESH_TIME, time) }
    }

    fun getLastCacheRefreshTime(): Long = preference.getLong(KEY_LAST_CACHE_REFRESH_TIME, 0L)

    fun getSharedPreferences(): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    // Ключи для наших настроек, по ним мы их будем получать
    companion object {
        const val PREFERENCES_NAME = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_DEFAULT_CATEGORY = "default_category"
        const val KEY_LAST_CACHE_REFRESH_TIME = "last_cache_refresh_time"
        private const val DEFAULT_CATEGORY = "popular"
    }
}