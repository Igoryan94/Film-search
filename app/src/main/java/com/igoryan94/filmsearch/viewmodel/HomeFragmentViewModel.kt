package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.data.PreferenceProvider
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class HomeFragmentViewModel(state: SavedStateHandle) : ViewModel() {
    private val savedStateHandle = state

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var preferenceProvider: PreferenceProvider

    val filmsListObserver = BehaviorSubject.create<List<Film>>()

    val showProgressBarObserver = BehaviorSubject.createDefault(false)

    val showErrorSnackbarObserver = PublishSubject.create<Boolean>()

    private val compositeDisposable = CompositeDisposable()

    init {
        App.instance.dagger.inject(this)
        getFilmsFromDB()
        getFilms()
    }

    private fun getFilmsFromDB() {
        val disposable = interactor.getFilmsFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { films -> filmsListObserver.onNext(films) }
        compositeDisposable.add(disposable)
    }

    fun getFilms() {
        showProgressBarObserver.onNext(true)

        interactor.getFilmsFromApi(1, object : ApiCallback {
            override fun onSuccess() {
                showProgressBarObserver.onNext(false)
            }

            override fun onFailure() {
                // Информируем подписчика о том, что возникла ошибка и надо это показать
                showErrorSnackbarObserver.onNext(true)

                // Получение фильмов из БД-кэша делается в фоне...
                // Загружаем фильмы из кэша лишь тогда, когда его актуальность менее 10 минут. Иначе полагаемся только на запрос из сети...
                val refreshCache = Observable.fromCallable {
                    val now = System.currentTimeMillis()
                    if (now - preferenceProvider.getLastCacheRefreshTime() > 1000 * 60 * 10L)
                        interactor.clearDB().subscribe() // No need to await for it...
                    showProgressBarObserver.onNext(false)
                }
                refreshCache
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
//                interactor.clearDB()
                // Можно раскомментировать строку выше, если будет нужна одноразовость считывания из базы...
            }
        })
    }

    fun resetErrorSnackbar() {
        showErrorSnackbarObserver.onNext(false)
    }

    fun saveState(list: List<Film>) {
        savedStateHandle["films_list"] = list
    }

    fun getState(): List<Film> {
        return savedStateHandle["films_list"] ?: mutableListOf()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    interface ApiCallback {
        fun onSuccess()
        fun onFailure()
    }
}