package com.igoryan94.filmsearch.domain

import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.ApiKey
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.data.entity.TmdbResultsDto
import com.igoryan94.filmsearch.di.modules.InteractorProvider
import com.igoryan94.filmsearch.di.modules.TmdbApiProvider
import com.igoryan94.filmsearch.utils.FilmDataConverter
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val tmdbApiProvider: TmdbApiProvider,
    private val preferences: PreferenceProvider
) : InteractorProvider {
    // В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    // и страницу, которую нужно загрузить (это для пагинации)
    override suspend fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        val retrofitService = tmdbApiProvider.provideTmdbApi()

        withContext(Dispatchers.IO) {
            // Метод getDefaultCategoryFromPreferences() будет нам получать при каждом запросе нужный нам список фильмов
            retrofitService.getFilms(getDefaultCategoryFromPreferences(), ApiKey.ID, "ru-RU", page)
                .enqueue(object :
                    Callback<TmdbResultsDto> {
                    override fun onResponse(
                        call: Call<TmdbResultsDto>,
                        response: Response<TmdbResultsDto>
                    ) {
                        CoroutineScope(Dispatchers.IO).launch {
                            // При успехе мы вызываем метод, передаем onSuccess и в этот коллбэк - список фильмов
                            val list =
                                FilmDataConverter.convertApiListToDtoList(response.body()?.tmdbFilms)

                            // Очищаем БД перед обновлением данных и кладем туда фильмы
                            repository.clearDB()
                            repository.putToDb(films = list)

                            callback.onSuccess()
                        }
                    }

                    override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                        // В случае провала вызываем другой метод коллбека
                        callback.onFailure()
                    }
                })
        }
    }

    // Метод для получения фильмов из базы, например при сетевой ошибке
    suspend fun getFilmsFromDB(): List<Film> =
        withContext(Dispatchers.IO) { repository.getAllFromDB() }

    // Метод для очистки базы данных
    suspend fun clearDB() = withContext(Dispatchers.IO) { repository.clearDB() }

    // Метод для сохранения настроек
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Метод для получения настроек
    fun getDefaultCategoryFromPreferences() = preferences.getDefaultCategory()
}