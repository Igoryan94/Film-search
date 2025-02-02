package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.domain.Interactor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class HomeFragmentViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    val filmsListData: Observable<List<Film>>
    val showProgressBar: BehaviorSubject<Boolean>

    init {
        App.instance.dagger.inject(this)
        showProgressBar = interactor.showProgressBarSubject
        filmsListData = interactor.getFilmsFromDB()
        getFilms()
    }

    fun getFilms(page: Int = 1) {
        interactor.getFilmsFromApi(page)
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