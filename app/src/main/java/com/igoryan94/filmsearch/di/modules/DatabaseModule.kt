package com.igoryan94.filmsearch.di.modules

import android.content.Context
import androidx.room.Room
import com.igoryan94.filmsearch.data.MainRepository
import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideFilmDao(context: Context) = Room.databaseBuilder(
        context, AppDatabase::class.java, "film_db"
    ).build().filmDao()

    @Singleton
    @Provides
    fun provideRepository(filmDao: FilmDao) = MainRepository(filmDao)
}