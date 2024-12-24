package com.igoryan94.filmsearch.data

import androidx.lifecycle.LiveData
import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.entity.Film
import java.util.concurrent.Executors
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val preferenceProvider: PreferenceProvider
) {
    fun putToDb(films: List<Film>) {
        // Запросы в БД должны быть в отдельном потоке. Также сохраняем время последнего обновления кэша.
        Executors.newSingleThreadExecutor().execute {
            filmDao.insertAll(films)
            preferenceProvider.saveLastCacheRefreshTime(System.currentTimeMillis())
        }
    }

    fun getAllFromDB(): LiveData<List<Film>> = filmDao.getCachedFilms()

    // Очистка базы
    fun clearDB() = Executors.newSingleThreadExecutor().execute { filmDao.clear() }
}