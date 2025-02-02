package com.igoryan94.filmsearch

import android.app.Application
import com.igoryan94.filmsearch.di.AppComponent
import com.igoryan94.filmsearch.di.DaggerAppComponent
import com.igoryan94.filmsearch.di.modules.DomainProvideModule
import com.igoryan94.remote_module.DaggerRemoteComponent
import timber.log.Timber

class App : Application() {
    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Инициализация Timber
        Timber.plant(Timber.DebugTree())

        // Создаем компонент Dagger
        val remoteProvider = DaggerRemoteComponent.create()
        dagger = DaggerAppComponent.builder()
            .remoteProvider(remoteProvider)
            .domainProvideModule(DomainProvideModule(this))
            .build()
    }

    companion object {
        lateinit var instance: App
    }
}