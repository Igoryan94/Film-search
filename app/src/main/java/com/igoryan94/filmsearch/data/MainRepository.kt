package com.igoryan94.filmsearch.data

import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.entity.Film
import java.util.concurrent.Executors
import javax.inject.Inject

class MainRepository @Inject constructor(private val filmDao: FilmDao) {
    fun putToDb(films: List<Film>) {
        // Запросы в БД должны быть в отдельном потоке
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
        }
    }

    fun getAllFromDB(): List<Film> {
        return filmDao.getCachedFilms()
    }

    // Очистка базы
    fun clearDB() = Executors.newSingleThreadExecutor().execute { filmDao.clear() }
}