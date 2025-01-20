package com.igoryan94.filmsearch.domain

import android.annotation.SuppressLint
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.ApiKey
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.data.entity.TmdbResultsDto
import com.igoryan94.filmsearch.di.modules.InteractorProvider
import com.igoryan94.filmsearch.di.modules.TmdbApiProvider
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val tmdbApiProvider: TmdbApiProvider,
    private val preferences: PreferenceProvider
) : InteractorProvider {
    // В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    // и страницу, которую нужно загрузить (это для пагинации)
    override fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        val retrofitService = tmdbApiProvider.provideTmdbApi()

        // Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
        retrofitService.getFilms(getDefaultCategoryFromPreferences(), ApiKey.ID, "ru-RU", page)
            .enqueue(object :
                Callback<TmdbResultsDto> {
                @SuppressLint("CheckResult")
                override fun onResponse(
                    call: Call<TmdbResultsDto>,
                    response: Response<TmdbResultsDto>
                ) {
                    // При успехе мы вызываем метод, передаем onSuccess, и в этот коллбэк - список фильмов
                    val filmsObservable: Observable<List<Film>> = Observable.fromCallable {
                        response.body()?.tmdbFilms ?: emptyList()
                    }
                        // Конвертируем полученные данные из исходного объекта List<TmdbFilm> в
                        // пригодный для нашей обработки List<Film>. Если ошибка в данных (null),
                        // то возвращаем пустой MutableList<Film>
                        .map { tmdbFilms ->
                            tmdbFilms.map { tmdbFilm ->
                                Film(
                                    title = tmdbFilm.title,
                                    poster = tmdbFilm.posterPath,
                                    description = tmdbFilm.overview,
                                    rating = tmdbFilm.voteAverage,
                                    isInFavorites = false
                                )
                            }
                        }

                    // Была добавлена постраничность: тут нам уже не нужно делать очистку БД перед вставкой, иначе мы потеряем старые данные
                    // Кладем в БД фильмы
                    filmsObservable
                        .flatMap { films -> repository.putToDb(films) }
                        .subscribeOn(Schedulers.io())
                        .subscribe({ }, { callback.onFailure() }, { callback.onSuccess() })
                }

                override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                    // В случае провала вызываем другой метод коллбека
                    callback.onFailure()
                }
            })
    }

    // Метод для получения фильмов из базы, например при сетевой ошибке
    fun getFilmsFromDB(): Observable<List<Film>> {
        Timber.d("getFilmsFromDB() called")
        return repository.getAllFromDB()
            .doOnNext { films ->
                Timber.d("getFilmsFromDB() return: ${films.size} film(s)")
            }
    }

    // Метод для очистки базы данных
    fun clearDB(): Observable<Unit> = repository.clearDB()


    // Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()
}