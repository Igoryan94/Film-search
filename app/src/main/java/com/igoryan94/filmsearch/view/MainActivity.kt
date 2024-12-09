package com.igoryan94.filmsearch.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.utils.toast
import com.igoryan94.filmsearch.view.fragments.FavoritesFragment
import com.igoryan94.filmsearch.view.fragments.FilmDetailsFragment
import com.igoryan94.filmsearch.view.fragments.HomeFragment
import com.igoryan94.filmsearch.view.fragments.SelectionsFragment
import com.igoryan94.filmsearch.view.fragments.SettingsFragment
import com.igoryan94.filmsearch.view.fragments.WatchLaterFragment
import com.igoryan94.filmsearch.view.recyclerview_adapters.Film
import com.igoryan94.filmsearch.view.training.activities.AnimCircularRevealActivity

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupHomeFragment()
        setupBottomNav()
    }

    // TODO перенести в одну из тестовых активити это:
    //  Сохранение изображения в MediaStore:
    //    private fun loadToGallery(bitmap: Bitmap) {
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    //            //Задаем информацию для файла
    //            val contentValues = ContentValues().apply {
    //                //Название файла
    //                put(MediaStore.Images.Media.DISPLAY_NAME, "Картинка-пример")
    //                //Описание
    //                put(MediaStore.Images.Media.DESCRIPTION, "Картинка")
    //                //Тип файла
    //                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    //                //Дата создания
    //                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
    //                //Опционально можно создать подпапку в галерее
    //                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
    //            }
    //            //Получаем экземпляр объекта ContentResolver
    //            val contentResolver = contentResolver
    //            //Указываем в какую коллекцию будем класть, в данном случае Images
    //            val uri = contentResolver.insert(
    //                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    //                contentValues
    //            )
    //            //Открываем поток
    //            contentResolver.openOutputStream(uri!!)?.let {
    //                //Загружаем картинку, опционально можно задать уровень компрессии
    //                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    //                //Закрываем поток
    //                it.close()
    //            }
    //        } else {
    //            @Suppress("DEPRECATION")
    //            MediaStore.Images.Media.insertImage(
    //                contentResolver,
    //                bitmap,
    //                "Картинка титул",
    //                "Картинка описание"
    //            )
    //        }
    //    }
    //  Пример использования:
    //    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.anim_set_horizon)
    //  loadToGallery(bitmap)

    private fun setupViews() {
        setSupportActionBar(b.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_vector_back)

        b.btnTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, AnimCircularRevealActivity::class.java))
        }

        b.topAppBar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount < 2) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else
                onBackPressedDispatcher.onBackPressed()
        }

        b.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> "Настройки".toast(this)
//                R.id.search -> "Поиск".toast(this)
                else -> "-".toast(this)
            }
            true
        }
    }

    // Инициализация центрального фрагмента для навигации по фильмам
    private fun setupHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentPlaceholder, HomeFragment())
            .addToBackStack(null)
            .commit()
    }

    // Настройка нижней навигации
    private fun setupBottomNav() {
        // Реакция на выбор элементов навигации
        b.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                //В первом параметре, если фрагмент не найден и метод вернул null, то с помощью
                //элвиса мы вызываем создание нового фрагмента
                R.id.home -> changeFragment(
                    checkFragmentExistence("home") ?: HomeFragment(),
                    "home"
                )

                R.id.favorites -> changeFragment(
                    checkFragmentExistence("favorites") ?: FavoritesFragment(), "favorites"
                )

                R.id.watchLater -> changeFragment(
                    checkFragmentExistence("watch_later") ?: WatchLaterFragment(), "watch_later"
                )

                R.id.selections -> changeFragment(
                    checkFragmentExistence("selections") ?: SelectionsFragment(), "selections"
                )

                R.id.settings -> changeFragment(
                    checkFragmentExistence("selections") ?: SettingsFragment(), "selections"
                )

                else -> return@setOnItemSelectedListener false
            }

            true
        }
    }

    // Открытие деталей фильма
    fun openFilmDetails(film: Film) {
        val bundle = Bundle()
        //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
        //передаваемый объект
        bundle.putParcelable("film", film)

        val fragment = FilmDetailsFragment()
        fragment.arguments = bundle

        changeFragment(fragment, fragment.toString())
    }

    //Ищем фрагмент по тегу, если он есть то возвращаем его, если нет, то null
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
}