package com.igoryan94.filmsearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.igoryan94.filmsearch.data.dao.FilmDao
import com.igoryan94.filmsearch.data.entity.Film

@Database(entities = [Film::class], version = 1, exportSchema = false)
// TODO настроить на работу с exportSchema = true
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
}