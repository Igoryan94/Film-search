package com.igoryan94.filmsearch.di.modules

import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.entity.API.TmdbApi
import com.igoryan94.filmsearch.data.entity.ApiConstants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RemoteModule {
    // Создаем клиент и добавляем туда перехватчик
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        // Настраиваем таймауты для медленного интернета
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо никакого.
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (App.isDebugging) level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    // Создаём объект Retrofit и передаём ему клиент
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        // Конвертер
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)
}