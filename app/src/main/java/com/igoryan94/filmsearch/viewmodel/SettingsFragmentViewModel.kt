package com.igoryan94.filmsearch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igoryan94.filmsearch.App
import com.igoryan94.filmsearch.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    // Инжектим интерактор
    @Inject
    lateinit var interactor: Interactor
    val categoryPropertyLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        App.instance.dagger.inject(this)
        // Получаем категорию при инициализации, чтобы у нас сразу подтягивалась категория
        getCategoryProperty()
    }

    private fun getCategoryProperty() {
        // Кладем категорию в LiveData
        categoryPropertyLiveData.value = interactor.getDefaultCategoryFromPreferences()
    }

    fun putCategoryProperty(category: String) {
        // Сохраняем в настройки
        interactor.saveDefaultCategoryToPreferences(category)
        // И сразу забираем, чтобы сохранить состояние в модели
        getCategoryProperty()
    }
}