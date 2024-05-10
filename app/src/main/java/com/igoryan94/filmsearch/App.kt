package com.igoryan94.filmsearch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(Timber.DebugTree())

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
    }

    class AppLifecycleObserver : LifecycleObserver {
        private val lifecycleEventObserver = LifecycleEventObserver { source, event ->
            when (event) {
                Lifecycle.Event.ON_START -> Timber.d("app lifecycle stage is at start")
                Lifecycle.Event.ON_PAUSE -> Timber.d("app lifecycle stage is pausing")
                Lifecycle.Event.ON_STOP -> Timber.d("app lifecycle stage is stopped")
                Lifecycle.Event.ON_CREATE -> Timber.d("app lifecycle stage is at creation")
                Lifecycle.Event.ON_RESUME -> Timber.d("app lifecycle stage is running")
                Lifecycle.Event.ON_DESTROY -> Timber.d("app lifecycle stage is destroying")
                else -> Timber.d("app lifecycle is at non processed stage: ${event.name}")
            }
        }

        init {
            ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }
}