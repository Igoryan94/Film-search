package com.igoryan94.filmsearch.domain

import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.entity.ApiKey
import com.igoryan94.filmsearch.data.entity.TmdbResultsDto
import com.igoryan94.filmsearch.di.modules.InteractorProvider
import com.igoryan94.filmsearch.di.modules.TmdbApiProvider
import com.igoryan94.filmsearch.utils.FilmDataConverter
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class Interactor @Inject constructor(
    private val repository: MainRepository,
    private val tmdbApiProvider: TmdbApiProvider
) : InteractorProvider {
    // В конструктор мы будем передавать коллбэк из вью модели, чтобы реагировать на то, когда фильмы будут получены
    // и страницу, которую нужно загрузить (это для пагинации)
    override fun getFilmsFromApi(page: Int, callback: HomeFragmentViewModel.ApiCallback) {
        val retrofitService = tmdbApiProvider.provideTmdbApi()

        retrofitService.getFilms(ApiKey.ID, "ru-RU", page).enqueue(object :
            Callback<TmdbResultsDto> {
            override fun onResponse(
                call: Call<TmdbResultsDto>,
                response: Response<TmdbResultsDto>
            ) {
                // При успехе мы вызываем метод передаем onSuccess и в этот коллбэк список фильмов
                callback.onSuccess(FilmDataConverter.convertApiListToDtoList(response.body()?.tmdbFilms))
            }

            override fun onFailure(call: Call<TmdbResultsDto>, t: Throwable) {
                // В случае провала вызываем другой метод коллбека
                callback.onFailure()
            }
        })
    }
}