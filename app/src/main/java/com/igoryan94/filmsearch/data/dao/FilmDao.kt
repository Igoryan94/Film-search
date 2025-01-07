package com.igoryan94.filmsearch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.igoryan94.filmsearch.data.entity.Film

// Room: помечаем аннотацией, что это Data(base)AccessObject
@Dao
interface FilmDao {
    // Запрос на всю таблицу
    @Query("SELECT * FROM cached_films")
    fun getCachedFilms(): List<Film>

    // Кладём список в БД, в случае конфликта перезаписываем
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Film>)

    @Query("DELETE FROM cached_films")
    fun clear()
}