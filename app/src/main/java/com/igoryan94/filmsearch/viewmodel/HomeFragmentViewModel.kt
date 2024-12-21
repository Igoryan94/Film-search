package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.domain.Interactor
import java.util.concurrent.Executors
import javax.inject.Inject

class HomeFragmentViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    val filmsListLiveData = MutableLiveData<List<Film>>()

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    init {
        App.instance.dagger.inject(this)
        getFilms()
    }

    fun getFilms() {
        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
            }

            override fun onFailure() {
                // Получение фильмов из БД-кэша делается в фоне...
                Executors.newSingleThreadExecutor().execute {
                    // Загружаем фильмы из кэша лишь тогда, когда его актуальность менее 10 минут. Иначе полагаемся только на запрос из сети...
                    val now = System.currentTimeMillis()
                    if (now - preferenceProvider.getLastCacheRefreshTime() > 1000 * 60 * 10L)
                        interactor.clearDB()
                    else filmsListLiveData.postValue(interactor.getFilmsFromDB())
                }
                //interactor.clearDB()
                // Можно раскомментировать строку выше, если будет нужна одноразовость считывания из базы...
            }
        })
    }

    fun saveState(list: List<Film>) {
        savedStateHandle["films_list"] = list
    }

    fun getState(): List<Film> {
        return savedStateHandle["films_list"] ?: mutableListOf()
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}