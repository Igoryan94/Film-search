package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.di.modules.InteractorProvider
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()

    @Inject
    lateinit var interactor: InteractorProvider

    init {
        App.instance.dagger.inject(this)

        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess(films: List<Film>) {
                filmsListLiveData.postValue(films)
            }

            override fun onFailure() {
            }
        })
    }

    interface ApiCallback {
        fun onSuccess(films: List<Film>)
        fun onFailure()
    }
}