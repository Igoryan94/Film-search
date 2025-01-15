package com.igoryan94.filmsearch.data

import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.entity.Film
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val filmDao: FilmDao,
    private val preferenceProvider: PreferenceProvider
) {
    fun putToDb(films: List<Film>): Observable<Unit> {
        // Запросы в БД должны быть в отдельном потоке. Также сохраняем время последнего обновления кэша.
        return Observable.fromCallable {
            filmDao.insertAll(films)
            preferenceProvider.saveLastCacheRefreshTime(System.currentTimeMillis())
        }.subscribeOn(Schedulers.io())
    }

    fun getAllFromDB(): Observable<List<Film>> =
        Observable.fromCallable { filmDao.getCachedFilms() }.subscribeOn(Schedulers.io())


    // Очистка базы
    fun clearDB(): Observable<Unit> =
        Observable.fromCallable { filmDao.clear() }.subscribeOn(Schedulers.io())
}