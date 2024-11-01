package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.domain.Interactor
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private var interactor: Interactor = App.instance.interactor

    init {
        val films = interactor.getFilmsDB()
        filmsListLiveData.postValue(films)
    }
}