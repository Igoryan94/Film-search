package com.igoryan94.filmsearch.data

import com.igoryan94.filmsearch.di.modules.RepositoryFactory
import javax.inject.Inject

class MainRepository @Inject constructor() : RepositoryFactory {
    val filmsDataBase = listOf<String>()

    override fun create(): MainRepository {
        return this
    }
}