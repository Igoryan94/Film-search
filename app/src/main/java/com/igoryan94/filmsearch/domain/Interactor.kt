package com.igoryan94.filmsearch.domain

import android.annotation.SuppressLint
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.ApiKey
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.di.modules.InteractorProvider
import com.igoryan94.remote_module.TmdbApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val tmdbApi: TmdbApi,
    private val preferences: PreferenceProvider
) : InteractorProvider {
    val showProgressBarSubject = BehaviorSubject.create<Boolean>()

    // В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    // и страницу, которую нужно загрузить (это для пагинации)
    @SuppressLint("CheckResult")
    override fun getFilmsFromApi(page: Int) {
        // Показываем ProgressBar
        showProgressBarSubject.onNext(true)

        // Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        tmdbApi.getFilms(getDefaultCategoryFromPreferences(), ApiKey.ID, "ru-RU", page)
            .subscribeOn(Schedulers.io())
            .map {
                it.tmdbFilms.map { tmdbFilm ->
                    Film(
                        title = tmdbFilm.title,
                        poster = tmdbFilm.posterPath,
                        description = tmdbFilm.overview,
                        rating = tmdbFilm.voteAverage,
                        isInFavorites = false
                    )
                }
            }.subscribe(
                {
                    showProgressBarSubject.onNext(false)
                    repository.putToDb(it)
                },
                {
                    showProgressBarSubject.onNext(false)
                }
            )
    }

    // Метод для очистки базы данных
    fun clearDB(): Observable<Unit> = repository.clearDB()


    // Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()

    fun getFilmsFromDB(): Observable<List<Film>> = repository.getAllFromDB()
}