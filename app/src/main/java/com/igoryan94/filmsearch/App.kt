package com.igoryan94.filmsearch

import android.app.Application
import com.igoryan94.filmsearch.di.DI
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //Прикрепляем контекст
//            androidContext(this@App) // FIXME Не существует!
            //(Опционально) подключаем зависимость
//            androidLogger() // FIXME Не существует!
            //Инициализируем модули
            modules(listOf(DI.mainModule))
        }

        // Инициализация Timber
        if (isDebugging) Timber.plant(Timber.DebugTree())
    }

    companion object {
        val isDebugging = true // BuildConfig.DEBUG
    }
}