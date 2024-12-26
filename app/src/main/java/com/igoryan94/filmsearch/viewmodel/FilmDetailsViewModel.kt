package com.igoryan94.filmsearch.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import timber.log.Timber
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FilmDetailsViewModel : ViewModel() {
    suspend fun loadWallpaper(url: String): Bitmap? = suspendCoroutine {
        val bitmap = try {
            val url = URL(url)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            // Залогировать ошибку
            Timber.e("Error loading wallpaper: ${e.message}")
            null // Вернуть null в случае ошибки
        }
        it.resume(bitmap)
    }
}