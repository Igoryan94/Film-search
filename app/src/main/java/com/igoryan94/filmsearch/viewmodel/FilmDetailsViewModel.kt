package com.igoryan94.filmsearch.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.net.URL

class FilmDetailsViewModel : ViewModel() {
    fun loadWallpaper(url: String): Observable<Bitmap> {
        return Observable.fromCallable {
            val url = URL(url)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
            .subscribeOn(Schedulers.io())
            .onErrorReturn { e ->
                Timber.e("Error loading wallpaper: ${e.message}")
                //Если пришел null, мы возвращаем  just(null)
                null
            }
    }
}