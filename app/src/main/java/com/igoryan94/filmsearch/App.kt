package com.igoryan94.filmsearch

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Инициализация Timber
        if (isDebugging) Timber.plant(Timber.DebugTree())
    }

    companion object {
        val isDebugging = true // BuildConfig.DEBUG
    }
}