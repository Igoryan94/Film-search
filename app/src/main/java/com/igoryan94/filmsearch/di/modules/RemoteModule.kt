package com.igoryan94.filmsearch.di.modules

import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.entity.API.TmdbApi
import com.igoryan94.filmsearch.data.entity.ApiConstants
import dagger.Binds
import dagger.Module
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Интерфейсы, которые будут реализовывать классы
interface OkHttpClientProvider {
    fun provideHttpsClient(): OkHttpClient
}

interface RetrofitProvider {
    fun provideRetrofitInstance(): Retrofit
}

interface TmdbApiProvider {
    fun provideTmdbApi(): TmdbApi
}

class HttpClientFactory @Inject constructor() : OkHttpClientProvider {
    // Создаем клиент https
    override fun provideHttpsClient(): OkHttpClient = OkHttpClient.Builder()
        // Настраиваем таймауты для медленного интернета
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        // Создаём перехватчик. Если приложение собрано для отладки, включаем базовый логгинг вместо никакого.
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (App.isDebugging) level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
}

class RetrofitFactory @Inject constructor(private val okHttpClientProvider: OkHttpClientProvider) :
    RetrofitProvider {
    // Создаём объект Retrofit и передаём ему клиент
    override fun provideRetrofitInstance(): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        // Конвертер
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientProvider.provideHttpsClient())
        .build()
}

class TmdbApiFactory @Inject constructor(private val retrofitProvider: RetrofitProvider) :
    TmdbApiProvider {
    override fun provideTmdbApi(): TmdbApi =
        retrofitProvider.provideRetrofitInstance().create(TmdbApi::class.java)
}

@Module
@Suppress("unused")
abstract class RemoteModule {
    @Singleton
    @Binds
    abstract fun bindHttpClient(httpClientFactory: HttpClientFactory): OkHttpClientProvider

    @Singleton
    @Binds
    abstract fun bindRetrofit(retrofitFactory: RetrofitFactory): RetrofitProvider

    @Singleton
    @Binds
    abstract fun bindTmdbApi(tmdbApiFactory: TmdbApiFactory): TmdbApiProvider
}