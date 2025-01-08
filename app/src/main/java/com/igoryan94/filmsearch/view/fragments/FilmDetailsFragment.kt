package com.igoryan94.filmsearch.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.ApiConstants
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.databinding.FragmentFilmDetailsBinding
import com.igoryan94.filmsearch.viewmodel.FilmDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class FilmDetailsFragment : Fragment() {
    lateinit var b: FragmentFilmDetailsBinding

    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var film: Film

    private val filmDetailsViewModel: FilmDetailsViewModel by activityViewModels()

    init {
        enterTransition = Slide(Gravity.END).apply { duration = 800 }
        returnTransition = Slide(Gravity.END).apply {
            duration = 800
            mode = Slide.MODE_OUT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentFilmDetailsBinding.inflate(inflater, container, false)

        setupDetails()
        setupDownloadFab()
        setupFavFab()
        setupShareFab()

        return b.root
    }

    @Suppress("DEPRECATION")
    private fun setupDetails() {
        film = (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable("film")
        else arguments?.getParcelable("film", Film::class.java))
            ?: Film(
                title = "(null)", poster = "",
                description = "Object is null"
            )

        b.detailsToolbar.title = film.title
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
            .centerCrop()
            .into(b.detailsPoster)
        b.detailsDescription.text = film.description
    }

    private fun setupDownloadFab() {
        b.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }
    }

    private fun setupFavFab() {
        b.detailsFabFavorites.setImageResource(
            if (film.isInFavorites) R.drawable.ic_vector_fav_filled
            else R.drawable.ic_vector_fav
        )

        b.detailsFabFavorites.setOnClickListener {
            if (!film.isInFavorites) {
                b.detailsFabFavorites.setImageResource(R.drawable.ic_vector_fav)
                film.isInFavorites = true
            } else {
                b.detailsFabFavorites.setImageResource(R.drawable.ic_vector_fav_filled)
                film.isInFavorites = false
            }
        }
    }

    private fun setupShareFab() {
        b.detailsFabShare.setOnClickListener {
            // Создаем интент
            val intent = Intent()
            // Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            // Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this film: ${film.title}\n\n${film.description}"
            )
            // Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = "text/plain"
            // Запускаем наше активити
            startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }

    // Узнаем, было ли получено разрешение ранее
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Запрашиваем разрешение
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun String.handleSingleQuote(): String = this.replace("'", "")
    private fun saveToGallery(bitmap: Bitmap?): Uri? {
        Timber.d("saveToGallery: start")
        if (bitmap == null) {
            Timber.d("saveToGallery: bitmap is null, returning null")
            return null
        }

        val contentResolver = requireActivity().contentResolver
        Timber.d("saveToGallery: contentResolver obtained")
        val imageName = film.title.handleSingleQuote()
        Timber.d("saveToGallery: imageName = $imageName")
        val imageCollection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        Timber.d("saveToGallery: imageCollection obtained")


        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName + ".jpg")
            Timber.d("saveToGallery: DISPLAY_NAME added to ContentValues")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            Timber.d("saveToGallery: MIME_TYPE added to ContentValues")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            Timber.d("saveToGallery: RELATIVE_PATH added to ContentValues")
            put(MediaStore.Images.Media.IS_PENDING, 1) // Помечаем как "в ожидании"
            Timber.d("saveToGallery: IS_PENDING set to 1 in ContentValues")
        }

        var imageUri: Uri?
        try {
            Timber.d("saveToGallery: trying to insert image")
            imageUri = contentResolver.insert(imageCollection, contentValues)
            Timber.d("saveToGallery: imageUri = $imageUri")
            imageUri?.let { uri ->
                Timber.d("saveToGallery: imageUri is not null")
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    Timber.d("saveToGallery: outputStream opened")
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Timber.d("saveToGallery: bitmap compressed and written to outputStream")
                }
                contentValues.clear()
                Timber.d("saveToGallery: contentValues cleared")
                contentValues.put(
                    MediaStore.Images.Media.IS_PENDING,
                    0
                ) // Отмечаем как "не в ожидании"
                Timber.d("saveToGallery: IS_PENDING set to 0 in ContentValues")
                contentResolver.update(uri, contentValues, null, null)
                Timber.d("saveToGallery: contentResolver updated")
            }
        } catch (e: Exception) {
            Timber.d("saveToGallery: Exception occurred: ${e.message}")
            // Обработка ошибки сохранения, например, логирование
            e.printStackTrace()
            // Если произошла ошибка, возвращаем null
            Timber.d("saveToGallery: returning null because of exception")
            return null
        }
        Timber.d("saveToGallery: returning imageUri = $imageUri")
        return imageUri
    }

    private fun performAsyncLoadOfPoster() {
        Timber.d("performAsyncLoadOfPoster: start")
        // Проверяем есть ли разрешение
        MainScope().launch {
            Timber.d("performAsyncLoadOfPoster: launch coroutine in MainScope")
            // Включаем Прогресс-бар
            b.progressBar.isVisible = true
            Timber.d("performAsyncLoadOfPoster: progress bar is visible")
            try {
                Timber.d("performAsyncLoadOfPoster: try block start")
                // Создаем через async, так как нам нужен результат от работы, то есть Bitmap
                val job = scope.async {
                    Timber.d("performAsyncLoadOfPoster: async job started")
                    filmDetailsViewModel.loadWallpaper(ApiConstants.IMAGES_URL + "original" + film.poster)
                }
                // Получаем результат загрузки
                val loadedBitmap = job.await()
                Timber.d("performAsyncLoadOfPoster: loadedBitmap obtained")

                // Сохраняем в галерею и получаем URI
                val savedImageUri = saveToGallery(loadedBitmap)
                Timber.d("performAsyncLoadOfPoster: savedImageUri = $savedImageUri")

                // FIXME Snackbar отображается строго под панелью навигации родительской активити - исправить...

                if (savedImageUri != null) {
                    Timber.d("performAsyncLoadOfPoster: savedImageUri is not null, showing success snackbar")
                    // Выводим снекбар с кнопкой перейти в галерею
                    Snackbar.make(
                        b.root, R.string.downloaded_to_gallery,
                        Snackbar.LENGTH_LONG
                    ).setAction(R.string.open) {
                        Timber.d("performAsyncLoadOfPoster: open gallery action clicked")
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            Timber.d("performAsyncLoadOfPoster: Intent action set to ACTION_VIEW")
                            data = savedImageUri // Устанавливаем URI сохраненного файла
                            Timber.d("performAsyncLoadOfPoster: Intent data set to savedImageUri")
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION // Необходимые флаги
                            Timber.d("performAsyncLoadOfPoster: Intent flags set")
                            startActivity(this)
                            Timber.d("performAsyncLoadOfPoster: startActivity called")
                        }
                    }.show()

                    Timber.d("performAsyncLoadOfPoster: snackbar with open action shown")
                } else {
                    Timber.d("performAsyncLoadOfPoster: savedImageUri is null, showing failure snackbar")
                    // Обработка случая, когда сохранение не удалось
                    Snackbar.make(
                        b.root, R.string.save_failed, // Добавьте эту строку в ресурсы
                        Snackbar.LENGTH_LONG
                    ).show()
                    Timber.d("performAsyncLoadOfPoster: snackbar with save failed message shown")
                }

            } catch (e: Exception) {
                Timber.d("performAsyncLoadOfPoster: Exception occurred: ${e.message}")
                // Обрабатываем любые исключения, которые могли возникнуть при загрузке
                Snackbar.make(
                    b.root, R.string.download_failed,
                    Snackbar.LENGTH_LONG
                ).show()
                Timber.d("performAsyncLoadOfPoster: snackbar with download failed message shown")
                // Логируем ошибку для отладки
                e.printStackTrace()
            } finally {
                Timber.d("performAsyncLoadOfPoster: finally block, hiding progress bar")
                // Отключаем Прогресс-бар в любом случае (успех или ошибка)
                b.progressBar.isVisible = false
                Timber.d("performAsyncLoadOfPoster: progress bar is hidden")
            }
        }
        Timber.d("performAsyncLoadOfPoster: end")
    }
}