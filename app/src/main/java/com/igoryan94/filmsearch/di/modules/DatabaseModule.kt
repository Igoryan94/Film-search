package com.igoryan94.filmsearch.di.modules

import com.igoryan94.filmsearch.data.MainRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

interface RepositoryFactory {
    fun create(): MainRepository
}

@Module
abstract class DatabaseModule {
    @Suppress("unused")
    @Binds
    @Singleton
    abstract fun bindRepository(mainRepository: MainRepository): RepositoryFactory
}