package com.igoryan94.filmsearch.di

import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.entity.API.TmdbApi
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.domain.Interactor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DI {
    val mainModule = module {
        // Инициализируем репозиторий
        single { MainRepository() }

        single<TmdbApi> {
            // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо никакого.
            val interceptor = HttpLoggingInterceptor().apply {
                if (App.isDebugging) level = HttpLoggingInterceptor.Level.BASIC
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
            retrofit.create(TmdbApi::class.java)
        }

        // Инициализируем интерактор
        single { Interactor(get(), get()) }
    }
}