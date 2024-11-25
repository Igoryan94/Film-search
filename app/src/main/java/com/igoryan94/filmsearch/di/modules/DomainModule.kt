package com.igoryan94.filmsearch.di.modules

import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.entity.API
import com.igoryan94.filmsearch.domain.Interactor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    fun provideInteractor(repository: MainRepository, tmdbApi: API.TmdbApi) =
        Interactor(repository = repository, retrofitService = tmdbApi)
}