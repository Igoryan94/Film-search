package com.igoryan94.filmsearch.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
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
import com.igoryan94.filmsearch.FilmNotificationReceiver
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.data.entity.Film
import com.igoryan94.filmsearch.databinding.FragmentFilmDetailsBinding
import com.igoryan94.filmsearch.viewmodel.FilmDetailsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.Calendar

@Suppress("unused", "SpellCheckingInspection")
class FilmDetailsFragment : Fragment() {
    lateinit var b: FragmentFilmDetailsBinding

    private lateinit var film: Film

    private val filmDetailsViewModel: FilmDetailsViewModel by activityViewModels()

    private var disposable: Disposable? = null

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
        setupNotifFab()
        setupShareFab()

        return b.root
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
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
            .load(com.igoryan94.remote_module.entity.ApiConstants.IMAGES_URL + "w780" + film.poster)
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

    private fun setupNotifFab() {
        b.detailsFabSendCurrentFilmNotification.setOnClickListener {
            showTimePickerDialog() // Вызываем функцию показа TimePickerDialog
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                scheduleNotification(
                    selectedHour,
                    selectedMinute
                ) // Вызываем функцию планирования уведомления
            },
            hour,
            minute,
            true // true для 24-часового формата
        )
        timePickerDialog.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Если выбранное время уже прошло, переносим уведомление на следующий день
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(
            requireContext(),
            FilmNotificationReceiver::class.java
        ) // Используем BroadcastReceiver для уведомления
        intent.putExtra("film_title", film.title) // Передаем данные о фильме
        intent.putExtra("film_description", film.description)
        intent.putExtra("film_poster_path", film.poster)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            film.id, // Используем id фильма как requestCode, чтобы сделать PendingIntent уникальным (важно для редактирования/отмены)
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT // Важно для безопасности и корректной работы
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Snackbar.make(
            b.root,
            getString(
                R.string.notification_scheduled_snackbar,
                "${hour}:${minute}"
            ), // Сообщение об успехе
            Snackbar.LENGTH_LONG
        ).show()
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
            put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.jpg")
            Timber.d("saveToGallery: DISPLAY_NAME added to ContentValues")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            Timber.d("saveToGallery: MIME_TYPE added to ContentValues")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            Timber.d("saveToGallery: RELATIVE_PATH added to ContentValues")
            put(MediaStore.Images.Media.IS_PENDING, 1) // Помечаем как "в ожидании"
            Timber.d("saveToGallery: IS_PENDING set to 1 in ContentValues")
        }

        val imageUri: Uri?
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
        disposable?.dispose()

        Timber.d("performAsyncLoadOfPoster: start")
        // Проверяем есть ли разрешение
        b.progressBar.isVisible = true
        Timber.d("performAsyncLoadOfPoster: progress bar is visible")
        disposable =
            filmDetailsViewModel.loadWallpaper(com.igoryan94.remote_module.entity.ApiConstants.IMAGES_URL + "original" + film.poster)
                .subscribeOn(Schedulers.io()) // Теперь вызываем subscribeOn в Fragment
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ loadedBitmap ->
                    Timber.d("performAsyncLoadOfPoster: loadedBitmap obtained")
                    val savedImageUri = saveToGallery(loadedBitmap)
                    Timber.d("performAsyncLoadOfPoster: savedImageUri = $savedImageUri")

                    if (savedImageUri != null) {
                        Timber.d("performAsyncLoadOfPoster: savedImageUri is not null, showing success snackbar")
                        Snackbar.make(
                            b.root, R.string.downloaded_to_gallery,
                            Snackbar.LENGTH_LONG
                        ).setAction(R.string.open) {
                            Timber.d("performAsyncLoadOfPoster: open gallery action clicked")
                            Intent().apply {
                                action = Intent.ACTION_VIEW
                                Timber.d("performAsyncLoadOfPoster: Intent action set to ACTION_VIEW")
                                data = savedImageUri
                                Timber.d("performAsyncLoadOfPoster: Intent data set to savedImageUri")
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                Timber.d("performAsyncLoadOfPoster: Intent flags set")
                                startActivity(this)
                                Timber.d("performAsyncLoadOfPoster: startActivity called")
                            }
                        }.show()
                        Timber.d("performAsyncLoadOfPoster: snackbar with open action shown")
                    } else {
                        Timber.d("performAsyncLoadOfPoster: savedImageUri is null, showing failure snackbar")
                        Snackbar.make(
                            b.root, R.string.save_failed,
                            Snackbar.LENGTH_LONG
                        ).show()
                        Timber.d("performAsyncLoadOfPoster: snackbar with save failed message shown")
                    }
                }, { throwable ->
                    Timber.d("performAsyncLoadOfPoster: Exception occurred: ${throwable.message}")
                    Snackbar.make(
                        b.root, R.string.download_failed,
                        Snackbar.LENGTH_LONG
                    ).show()
                    Timber.d("performAsyncLoadOfPoster: snackbar with download failed message shown")
                    throwable.printStackTrace() // Логирование ошибки
                }, {
                    Timber.d("performAsyncLoadOfPoster: finally block, hiding progress bar")
                    b.progressBar.isVisible = false // Отключаем прогресс бар
                    Timber.d("performAsyncLoadOfPoster: progress bar is hidden")
                })
    }

    // Класс данных для хранения информации об уведомлении
    data class ScheduledNotification(
        var filmId: Int,
        var filmTitle: String,
        var filmDescription: String,
        var filmPosterPath: String, // Сохраняем путь к постеру
        var hour: Int,
        var minute: Int,
        val requestCode: Int
    )
}