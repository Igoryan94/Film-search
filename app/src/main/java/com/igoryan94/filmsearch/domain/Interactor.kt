package com.igoryan94.filmsearch.domain

import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film

class Interactor(val repository: MainRepository) {
    fun getFilmsDB(): List<Film> = repository.filmsDataBase
}