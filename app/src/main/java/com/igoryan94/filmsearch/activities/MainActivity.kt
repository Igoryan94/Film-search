package com.igoryan94.filmsearch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.igoryan94.filmsearch.R
import com.igoryan94.filmsearch.activities.training.AnimCircularRevealActivity
import com.igoryan94.filmsearch.databinding.ActivityMainBinding
import com.igoryan94.filmsearch.fragments.FavoritesFragment
import com.igoryan94.filmsearch.fragments.FilmDetailsFragment
import com.igoryan94.filmsearch.fragments.HomeFragment
import com.igoryan94.filmsearch.fragments.SelectionsFragment
import com.igoryan94.filmsearch.fragments.WatchLaterFragment
import com.igoryan94.filmsearch.toast
import com.igoryan94.filmsearch.views.recycler.adapters.Film

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

    // При создании опций меню...
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        //"Надуваем" наше меню
//        menuInflater.inflate(R.menu.main_top_bar, menu)
//        //Находим наш пункт меню с поиском
//        val menuItem = menu.findItem(R.id.search)
//        //Привязываем его как поле для поиска
//        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
//        //Задаем слушатель изменений ввода текста
//        searchView.setOnQueryTextListener(object : OnQueryTextListener {
//            val list = HomeFragment.instance.filmsDataBase.map { it.title }
//
//            //Здесь выполняется код при любом изменении текста
//            override fun onQueryTextChange(newText: String?): Boolean {
////                if (list.contains(newText)) "In list".toast(this@MainActivity)
////                else "Not in list".toast(this@MainActivity)
//                return false
//            }
//
//            //Здесь выполняется код по нажатию на кнопку поиска
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (list.stream()
//                        .anyMatch { it.contains(query ?: "(null)") }
//                ) "In list".toast(this@MainActivity)
//                else "Not in list".toast(this@MainActivity)
//                return false
//            }
//        })
        return super.onCreateOptionsMenu(menu)
    }

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