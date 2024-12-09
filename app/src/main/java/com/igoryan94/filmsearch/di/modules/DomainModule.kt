package com.igoryan94.filmsearch.di.modules

import android.content.Context
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.domain.Interactor
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DomainModule {
    @Suppress("unused")
    @Singleton
    @Binds
    abstract fun bindInteractor(interactor: Interactor): InteractorProvider
}

interface InteractorProvider {
    fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback)
}

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