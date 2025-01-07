package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragmentViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    private val _filmsListStateFlow = MutableStateFlow<List<Film>>(emptyList())
    val filmsListStateFlow: StateFlow<List<Film>> = _filmsListStateFlow.asStateFlow()

    private val _showProgressBarStateFlow = MutableStateFlow(false)
    val showProgressBarStateFlow: StateFlow<Boolean> = _showProgressBarStateFlow.asStateFlow()

    private val _showErrorSnackbarStateFlow = MutableStateFlow(false)
    val showErrorSnackbarStateFlow: StateFlow<Boolean> = _showErrorSnackbarStateFlow.asStateFlow()

    private val progressBarChannel = Channel<Boolean>(Channel.CONFLATED)

    init {
        App.instance.dagger.inject(this)
        getFilmsFromDB()
        getFilms()
    }

    private fun getFilmsFromDB() {
        viewModelScope.launch {
            _filmsListStateFlow.value = interactor.getFilmsFromDB()
        }
    }

    fun getFilms() {
        viewModelScope.launch {
            _showProgressBarStateFlow.value = true

            interactor.getFilmsFromApi(1, object : ApiCallback {
                override fun onSuccess() {
                    _showProgressBarStateFlow.value = false
                }

                override fun onFailure() {
                    // Информируем подписчика о том, что возникла ошибка и надо это показать
                    _showErrorSnackbarStateFlow.value = true

                    // Получение фильмов из БД-кэша делается в фоне...
                    viewModelScope.launch(Dispatchers.IO) {
                        // Загружаем фильмы из кэша лишь тогда, когда его актуальность менее 10 минут. Иначе полагаемся только на запрос из сети...
                        val now = System.currentTimeMillis()
                        if (now - preferenceProvider.getLastCacheRefreshTime() > 1000 * 60 * 10L)
                            interactor.clearDB()

                        _showProgressBarStateFlow.value = false
                    }
                    //interactor.clearDB()
                    // Можно раскомментировать строку выше, если будет нужна одноразовость считывания из базы...
                }
            })
        }
    }

    fun resetErrorSnackbar() {
        _showErrorSnackbarStateFlow.value = false
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