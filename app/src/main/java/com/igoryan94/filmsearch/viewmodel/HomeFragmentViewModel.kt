package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.domain.Interactor
import com.igoryan94.filmsearch.utils.SingleLiveEvent
import java.util.concurrent.Executors
import javax.inject.Inject

class HomeFragmentViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    @Inject
    lateinit var interactor: Interactor
    val filmsListLiveData: LiveData<List<Film>>
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorSnackbar: MutableLiveData<Boolean> = SingleLiveEvent()

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    init {
        App.instance.dagger.inject(this)
        filmsListLiveData = interactor.getFilmsFromDB()
        getFilms()
    }

    fun getFilms() {
        showProgressBar.postValue(true)

        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess() {
                showProgressBar.postValue(false)
            }

            override fun onFailure() {
                // Информируем подписчика о том, что возникла ошибка и надо это показать
                showErrorSnackbar.postValue(true)

                // Получение фильмов из БД-кэша делается в фоне...
                Executors.newSingleThreadExecutor().execute {
                    // Загружаем фильмы из кэша лишь тогда, когда его актуальность менее 10 минут. Иначе полагаемся только на запрос из сети...
                    val now = System.currentTimeMillis()
                    if (now - preferenceProvider.getLastCacheRefreshTime() > 1000 * 60 * 10L)
                        interactor.clearDB()

                    showProgressBar.postValue(false)
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
        fun onSuccess()
        fun onFailure()
    }
}