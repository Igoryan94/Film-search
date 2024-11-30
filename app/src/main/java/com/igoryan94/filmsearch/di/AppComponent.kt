package com.igoryan94.filmsearch.di

import com.igoryan94.filmsearch.di.modules.DatabaseModule
import com.igoryan94.filmsearch.di.modules.DomainModule
import com.igoryan94.filmsearch.di.modules.LazyDomainModule
import com.igoryan94.filmsearch.di.modules.RemoteModule
import com.igoryan94.filmsearch.viewmodel.HomeFragmentViewModel
import com.igoryan94.filmsearch.viewmodel.SettingsFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    // Внедряем все модули, нужные для этого компонента
    modules = [
        DatabaseModule::class,
        DomainModule::class,
        LazyDomainModule::class,
        RemoteModule::class
    ]
)
interface AppComponent {
    // Метод для того, чтобы появилась возможность внедрять зависимости в HomeFragmentViewModel
    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    // Метод для того, чтобы появилась возможность внедрять зависимости в SettingsFragmentViewModel
    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)
}