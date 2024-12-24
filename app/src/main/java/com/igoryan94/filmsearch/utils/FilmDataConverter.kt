package com.igoryan94.filmsearch.utils

import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.data.entity.TmdbFilm

object FilmDataConverter {
    fun convertApiListToDtoList(list: List<TmdbFilm>?) = list?.map {
        Film(
            title = it.title,
            poster = it.posterPath,
            description = it.overview,
            rating = it.voteAverage,
            isInFavorites = false
        )
    }?.toMutableList() ?: mutableListOf()
}