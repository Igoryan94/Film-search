package com.igoryan94.filmsearch.data

import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.entity.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val preferenceProvider: PreferenceProvider
) {
    suspend fun putToDb(films: List<Film>) {
        // Запросы в БД должны быть в отдельном потоке. Также сохраняем время последнего обновления кэша.
        withContext(Dispatchers.IO) {
            filmDao.insertAll(films)
            preferenceProvider.saveLastCacheRefreshTime(System.currentTimeMillis())
        }
    }

    suspend fun getAllFromDB(): List<Film> =
        withContext(Dispatchers.IO) { filmDao.getCachedFilms() }


    // Очистка базы
    suspend fun clearDB() = withContext(Dispatchers.IO) { filmDao.clear() }
}