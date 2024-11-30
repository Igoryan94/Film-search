package com.igoryan94.filmsearch.di.modules

import android.content.Context
import com.igoryan94.filmsearch.data.PreferenceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LazyDomainModule(val context: Context) {
    // Нам нужно контекст как-то провайдить, поэтому создаем такой метод
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    // Создаем экземпляр SharedPreferences
    fun providePreferences(context: Context) = PreferenceProvider(context)
}