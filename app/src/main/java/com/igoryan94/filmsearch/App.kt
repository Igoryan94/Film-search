package com.igoryan94.filmsearch

import android.app.Application
import com.igoryan94.filmsearch.di.AppComponent
import com.igoryan94.filmsearch.di.DaggerAppComponent
import timber.log.Timber

class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Инициализация Timber
        if (isDebugging) Timber.plant(Timber.DebugTree())

        // Создаем компонент Dagger
        dagger = DaggerAppComponent.create()
    }

    companion object {
        lateinit var instance: App
        val isDebugging = true // BuildConfig.DEBUG
    }
}