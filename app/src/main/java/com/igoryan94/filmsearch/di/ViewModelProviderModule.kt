package com.igoryan94.filmsearch.di

import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.entity.API.TmdbApi
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.domain.Interactor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(FragmentComponent::class)
object ViewModelProviderModule {
    @Singleton
    @Provides
    fun provideMainRepository(): MainRepository = MainRepository()

    @Singleton
    @Provides
    fun provideTmdbApi(): TmdbApi {
        // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо его отсутствия.
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
        return retrofit.create(TmdbApi::class.java)
    }

    @Singleton
    @Provides
    fun provideInteractor(mainRepository: MainRepository, tmdbApi: TmdbApi): Interactor =
        Interactor(mainRepository, tmdbApi)
}