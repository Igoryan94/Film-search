package com.igoryan94.filmsearch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.airbnb.lottie.BuildConfig
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.entity.API.TmdbApi
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App : Application() {
    lateinit var repo: MainRepository
    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()

        val isDebugging = BuildConfig.DEBUG

        // Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this

        // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо никакого.
        val interceptor = HttpLoggingInterceptor().apply {
            if (isDebugging) level = HttpLoggingInterceptor.Level.BASIC
        }
        // Создаем клиент и добавляем туда перехватчик
        val okHttpCLient = OkHttpClient.Builder()
            // Настраиваем таймауты для медленного интернета
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // Логгер
            .addInterceptor(interceptor)
            .build()

        // Создаём объект Retrofit и передаём ему клиент
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            // Конвертер
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpCLient)
            .build()
        val retrofitService = retrofit.create(TmdbApi::class.java)

        // Инициализируем репозиторий
        repo = MainRepository()
        // Инициализируем интерактор
        interactor = Interactor(repo, retrofitService)

        if (isDebugging) Timber.plant(Timber.DebugTree())

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
    }

    class AppLifecycleObserver : LifecycleObserver {
        private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
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